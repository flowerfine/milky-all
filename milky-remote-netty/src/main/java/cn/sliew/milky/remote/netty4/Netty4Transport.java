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

import cn.sliew.milky.common.collect.SetOnce;
import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.remote.transport.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * There are 4 types of connections per node, low/med/high/ping. Low if for batch oriented APIs (like recovery or
 * batch) with high payload that will cause regular request. (like search or single index) to take
 * longer. Med is for the typical search / single doc index. And High for things like cluster state. Ping is reserved for
 * sending out ping requests to other nodes.
 */
public class Netty4Transport extends TcpTransport {

    private static final Logger logger = LoggerFactory.getLogger(Netty4Transport.class);

    static final AttributeKey<ChannelListener> CHANNEL_LISTENER_KEY = AttributeKey.newInstance("milky-remote-channel-listener");
    static final AttributeKey<Netty4TcpChannel> TCP_CHANNEL_KEY = AttributeKey.newInstance("milky-remote-tcp-channel");
    static final AttributeKey<SetOnce<Netty4TcpServerChannel>> TCP_SERVER_CHANNEL_KEY = AttributeKey.newInstance("milky-remote-tcp-server-channel");

    private final SetOnce<Netty4TcpServerChannel> tcpServerChannelHolder = new SetOnce<>();

    private final RecvByteBufAllocator recvByteBufAllocator;
    private volatile ServerBootstrap serverBootstrap;
    private volatile Bootstrap clientBootstrap;

    public Netty4Transport() {
        this.recvByteBufAllocator = new FixedRecvByteBufAllocator(1024);
        createServerBootstrap();
        createClientBootstrap();
    }

    private void createClientBootstrap() {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1));

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);

        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, recvByteBufAllocator);

        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        this.clientBootstrap = bootstrap;
    }

    private void createServerBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(8));

        // NettyAllocator will return the channel type designed to work with the configuredAllocator
        serverBootstrap.channel(NioServerSocketChannel.class);

        // Set the allocators for both the server channel and the child channels created
        serverBootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT);

        serverBootstrap.handler(new ServerChannelExceptionHandler());
        serverBootstrap.childHandler(new ServerChannelInitializer());

        serverBootstrap.childAttr(TCP_SERVER_CHANNEL_KEY, tcpServerChannelHolder);

        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        serverBootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, recvByteBufAllocator);
        serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, recvByteBufAllocator);

        serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.validate();
        this.serverBootstrap = serverBootstrap;
    }

    @Override
    protected TcpChannel connect(Node node, ChannelListener listener) throws IOException {
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
        channel.attr(CHANNEL_LISTENER_KEY).set(listener);
        return nettyChannel;
    }

    @Override
    protected Netty4TcpServerChannel doBind(InetSocketAddress address, ChannelListener listener) throws IOException {
        serverBootstrap.childAttr(CHANNEL_LISTENER_KEY, listener);
        Channel channel = serverBootstrap.bind(address).syncUninterruptibly().channel();
        Netty4TcpServerChannel tcpServerChannel = new Netty4TcpServerChannel(channel);
        tcpServerChannelHolder.set(tcpServerChannel);
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
            SetOnce<Netty4TcpServerChannel> serverChannel = ch.attr(TCP_SERVER_CHANNEL_KEY).get();
            serverChannel.get().serverAcceptedChannel(nettyTcpChannel);
        }
    }


    @ChannelHandler.Sharable
    private class ServerChannelExceptionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            SetOnce<Netty4TcpServerChannel> serverChannel = ctx.channel().attr(TCP_SERVER_CHANNEL_KEY).get();
            if (cause instanceof Error) {
                onServerException(serverChannel.get(), new Exception(cause));
            } else {
                onServerException(serverChannel.get(), (Exception) cause);
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
                logger.error("exception while closing channel: {}", channel, f.cause());
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