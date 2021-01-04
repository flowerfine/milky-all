/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package cn.sliew.milky.remote.netty4;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.remote.transport.Node;
import cn.sliew.milky.remote.transport.TcpServerChannel;
import cn.sliew.milky.remote.transport.TcpTransport;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.util.List;

/**
 * There are 4 types of connections per node, low/med/high/ping. Low if for batch oriented APIs (like recovery or
 * batch) with high payload that will cause regular request. (like search or single index) to take
 * longer. Med is for the typical search / single doc index. And High for things like cluster state. Ping is reserved for
 * sending out ping requests to other nodes.
 */
public class Netty4Transport extends TcpTransport {

    private static final Logger logger = LoggerFactory.getLogger(Netty4Transport.class);

    static final AttributeKey<Netty4TcpChannel> TCP_CHANNEL_KEY = AttributeKey.newInstance("milky-remote-tcp-channel");
    static final AttributeKey<Netty4TcpServerChannel> TCP_SERVER_CHANNEL_KEY = AttributeKey.newInstance("milky-remote-tcp-server-channel");


    private final SharedGroupFactory sharedGroupFactory;
    private final RecvByteBufAllocator recvByteBufAllocator;
    private final ByteSizeValue receivePredictorMin;
    private final ByteSizeValue receivePredictorMax;
    private final ServerBootstrap serverBootstrap;
    private volatile Bootstrap clientBootstrap;
    private volatile SharedGroupFactory.SharedGroup sharedGroup;

    public Netty4Transport(Settings settings, Version version, ThreadPool threadPool, NetworkService networkService,
                           PageCacheRecycler pageCacheRecycler, NamedWriteableRegistry namedWriteableRegistry,
                           CircuitBreakerService circuitBreakerService, SharedGroupFactory sharedGroupFactory) {
        super(settings, version, threadPool, pageCacheRecycler, circuitBreakerService, namedWriteableRegistry, networkService);
        Netty4Utils.setAvailableProcessors(EsExecutors.NODE_PROCESSORS_SETTING.get(settings));
        NettyAllocator.logAllocatorDescriptionIfNeeded();
        this.sharedGroupFactory = sharedGroupFactory;

        // See AdaptiveReceiveBufferSizePredictor#DEFAULT_XXX for default values in netty..., we can use higher ones for us, even fixed one
        this.receivePredictorMin = NETTY_RECEIVE_PREDICTOR_MIN.get(settings);
        this.receivePredictorMax = NETTY_RECEIVE_PREDICTOR_MAX.get(settings);
        if (receivePredictorMax.getBytes() == receivePredictorMin.getBytes()) {
            recvByteBufAllocator = new FixedRecvByteBufAllocator((int) receivePredictorMax.getBytes());
        } else {
            recvByteBufAllocator = new AdaptiveRecvByteBufAllocator((int) receivePredictorMin.getBytes(),
                    (int) receivePredictorMin.getBytes(), (int) receivePredictorMax.getBytes());
        }
        createServerBootstrap(profileSettings, sharedGroup);

        bindServer(profileSettings);

        createClientBootstrap(sharedGroup);
    }

    private Bootstrap createClientBootstrap(SharedGroupFactory.SharedGroup sharedGroup) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(sharedGroup.getLowLevelGroup());

        // NettyAllocator will return the channel type designed to work with the configured allocator
        assert Netty4NioSocketChannel.class.isAssignableFrom(NettyAllocator.getChannelType());
        bootstrap.channel(NettyAllocator.getChannelType());
        bootstrap.option(ChannelOption.ALLOCATOR, NettyAllocator.getAllocator());

        bootstrap.option(ChannelOption.TCP_NODELAY, TransportSettings.TCP_NO_DELAY.get(settings));
        bootstrap.option(ChannelOption.SO_KEEPALIVE, TransportSettings.TCP_KEEP_ALIVE.get(settings));
        if (TransportSettings.TCP_KEEP_ALIVE.get(settings)) {
            // Note that Netty logs a warning if it can't set the option
            if (TransportSettings.TCP_KEEP_IDLE.get(settings) >= 0) {
                final SocketOption<Integer> keepIdleOption = NetUtils.getTcpKeepIdleSocketOptionOrNull();
                if (keepIdleOption != null) {
                    bootstrap.option(NioChannelOption.of(keepIdleOption), TransportSettings.TCP_KEEP_IDLE.get(settings));
                }
            }
            if (TransportSettings.TCP_KEEP_INTERVAL.get(settings) >= 0) {
                final SocketOption<Integer> keepIntervalOption = NetUtils.getTcpKeepIntervalSocketOptionOrNull();
                if (keepIntervalOption != null) {
                    bootstrap.option(NioChannelOption.of(keepIntervalOption), TransportSettings.TCP_KEEP_INTERVAL.get(settings));
                }
            }
            if (TransportSettings.TCP_KEEP_COUNT.get(settings) >= 0) {
                final SocketOption<Integer> keepCountOption = NetUtils.getTcpKeepCountSocketOptionOrNull();
                if (keepCountOption != null) {
                    bootstrap.option(NioChannelOption.of(keepCountOption), TransportSettings.TCP_KEEP_COUNT.get(settings));
                }
            }
        }

        final ByteSizeValue tcpSendBufferSize = TransportSettings.TCP_SEND_BUFFER_SIZE.get(settings);
        if (tcpSendBufferSize.getBytes() > 0) {
            bootstrap.option(ChannelOption.SO_SNDBUF, Math.toIntExact(tcpSendBufferSize.getBytes()));
        }

        final ByteSizeValue tcpReceiveBufferSize = TransportSettings.TCP_RECEIVE_BUFFER_SIZE.get(settings);
        if (tcpReceiveBufferSize.getBytes() > 0) {
            bootstrap.option(ChannelOption.SO_RCVBUF, Math.toIntExact(tcpReceiveBufferSize.getBytes()));
        }

        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, recvByteBufAllocator);

        final boolean reuseAddress = TransportSettings.TCP_REUSE_ADDRESS.get(settings);
        bootstrap.option(ChannelOption.SO_REUSEADDR, reuseAddress);

        return bootstrap;
    }

    private void createServerBootstrap(ProfileSettings profileSettings, SharedGroupFactory.SharedGroup sharedGroup) {
        String name = profileSettings.profileName;
        if (logger.isDebugEnabled()) {
            logger.debug("using profile[{}], worker_count[{}], port[{}], bind_host[{}], publish_host[{}], receive_predictor[{}->{}]",
                    name, sharedGroupFactory.getTransportWorkerCount(), profileSettings.portOrRange, profileSettings.bindHosts,
                    profileSettings.publishHosts, receivePredictorMin, receivePredictorMax);
        }

        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(sharedGroup.getLowLevelGroup());

        // NettyAllocator will return the channel type designed to work with the configuredAllocator
        serverBootstrap.channel(NettyAllocator.getServerChannelType());

        // Set the allocators for both the server channel and the child channels created
        serverBootstrap.option(ChannelOption.ALLOCATOR, NettyAllocator.getAllocator());
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, NettyAllocator.getAllocator());

        serverBootstrap.childHandler(getServerChannelInitializer(name));
        serverBootstrap.handler(new ServerChannelExceptionHandler());

        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, profileSettings.tcpNoDelay);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, profileSettings.tcpKeepAlive);
        if (profileSettings.tcpKeepAlive) {
            // Note that Netty logs a warning if it can't set the option
            if (profileSettings.tcpKeepIdle >= 0) {
                final SocketOption<Integer> keepIdleOption = NetUtils.getTcpKeepIdleSocketOptionOrNull();
                if (keepIdleOption != null) {
                    serverBootstrap.childOption(NioChannelOption.of(keepIdleOption), profileSettings.tcpKeepIdle);
                }
            }
            if (profileSettings.tcpKeepInterval >= 0) {
                final SocketOption<Integer> keepIntervalOption = NetUtils.getTcpKeepIntervalSocketOptionOrNull();
                if (keepIntervalOption != null) {
                    serverBootstrap.childOption(NioChannelOption.of(keepIntervalOption), profileSettings.tcpKeepInterval);
                }

            }
            if (profileSettings.tcpKeepCount >= 0) {
                final SocketOption<Integer> keepCountOption = NetUtils.getTcpKeepCountSocketOptionOrNull();
                if (keepCountOption != null) {
                    serverBootstrap.childOption(NioChannelOption.of(keepCountOption), profileSettings.tcpKeepCount);
                }
            }
        }

        if (profileSettings.sendBufferSize.getBytes() != -1) {
            serverBootstrap.childOption(ChannelOption.SO_SNDBUF, Math.toIntExact(profileSettings.sendBufferSize.getBytes()));
        }

        if (profileSettings.receiveBufferSize.getBytes() != -1) {
            serverBootstrap.childOption(ChannelOption.SO_RCVBUF, Math.toIntExact(profileSettings.receiveBufferSize.bytesAsInt()));
        }

        serverBootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, recvByteBufAllocator);
        serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, recvByteBufAllocator);

        serverBootstrap.option(ChannelOption.SO_REUSEADDR, profileSettings.reuseAddress);
        serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, profileSettings.reuseAddress);
        serverBootstrap.validate();

        serverBootstraps.put(name, serverBootstrap);
    }

    protected ChannelHandler getServerChannelInitializer() {
        return new ServerChannelInitializer();
    }

    @Override
    protected Netty4TcpChannel initiateChannel(Node node) throws IOException {
        InetSocketAddress address = InetSocketAddress.createUnresolved(node.getHostAddress(), node.getPort());
        Bootstrap bootstrapWithHandler = clientBootstrap.clone();
        bootstrapWithHandler.handler(new ClientChannelInitializer());
        bootstrapWithHandler.remoteAddress(address);
        ChannelFuture connectFuture = bootstrapWithHandler.connect();

        Channel channel = connectFuture.channel();
        if (channel == null) {
            throw new IOException(connectFuture.cause());
        }

        Netty4TcpChannel nettyChannel = new Netty4TcpChannel(channel, false, connectFuture);
        channel.attr(TCP_CHANNEL_KEY).set(nettyChannel);

        return nettyChannel;
    }

    protected Netty4TcpServerChannel bind(String name, InetSocketAddress address) {
        Channel channel = serverBootstrap.bind(address).syncUninterruptibly().channel();
        Netty4TcpServerChannel tcpServerChannel = new Netty4TcpServerChannel(channel);
        channel.attr(TCP_SERVER_CHANNEL_KEY).set(tcpServerChannel);
        return tcpServerChannel;
    }

    protected class ClientChannelInitializer extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            addClosedExceptionLogger(ch);
            ch.pipeline().addLast("dispatcher", new Netty4MessageChannelHandler(Netty4Transport.this));
        }
    }

    protected class ServerChannelInitializer extends ChannelInitializer<Channel> {
        private final NettyByteBufSizer sizer = new NettyByteBufSizer();

        @Override
        protected void initChannel(Channel ch) throws Exception {
            addClosedExceptionLogger(ch);
            Netty4TcpChannel nettyTcpChannel = new Netty4TcpChannel(ch, true, ch.newSucceededFuture());
            ch.attr(TCP_CHANNEL_KEY).set(nettyTcpChannel);
            ch.pipeline().addLast("byte_buf_sizer", sizer);
            ch.pipeline().addLast("dispatcher", new Netty4MessageChannelHandler(Netty4Transport.this));
            Netty4TcpServerChannel serverChannel = ch.attr(TCP_SERVER_CHANNEL_KEY).get();
            serverChannel.serverAcceptedChannel(nettyTcpChannel);
        }
    }


    @ChannelHandler.Sharable
    private class ServerChannelExceptionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            Netty4TcpServerChannel serverChannel = ctx.channel().attr(TCP_SERVER_CHANNEL_KEY).get();
            if (cause instanceof Error) {
                onServerException(serverChannel, new Exception(cause));
            } else {
                onServerException(serverChannel, (Exception) cause);
            }
        }
    }

    @ChannelHandler.Sharable
    public class NettyByteBufSizer extends MessageToMessageDecoder<ByteBuf> {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
            int readableBytes = buf.readableBytes();
            if (buf.capacity() >= 1024) {
                ByteBuf resized = buf.discardReadBytes().capacity(readableBytes);
                assert resized.readableBytes() == readableBytes;
                out.add(resized.retain());
            } else {
                out.add(buf.retain());
            }
        }
    }

    private void addClosedExceptionLogger(Channel channel) {
        channel.closeFuture().addListener(f -> {
            if (f.isSuccess() == false) {
                logger.debug("exception while closing channel: {}", channel, f.cause());
            }
        });
    }

    protected void onServerException(TcpServerChannel channel, Exception e) {
        if (e instanceof BindException) {
            logger.debug("bind exception from server channel caught on transport layer [{}]", channel, e);
        } else {
            logger.error("exception from server channel caught on transport layer [{}]", channel, e);
        }
    }
}