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

import cn.sliew.milky.remote.transport.ChannelListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A handler (must be the last one!) that does size based frame decoding and forwards the actual message
 * to the relevant action.
 */
final class Netty4MessageChannelHandler extends ChannelDuplexHandler {

    private final Netty4Transport transport;
    private final Queue<WriteOperation> queuedWrites = new ArrayDeque<>();
    private WriteOperation currentWrite;

    Netty4MessageChannelHandler(Netty4Transport transport) {
        this.transport = transport;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Netty4TcpChannel channel = ctx.channel().attr(Netty4Transport.TCP_CHANNEL_KEY).get();
        transport.getChannelListener().received(channel, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Netty4TcpChannel tcpChannel = ctx.channel().attr(Netty4Transport.TCP_CHANNEL_KEY).get();
        ChannelListener channelListener = transport.getChannelListener();
        if (cause instanceof Error) {
            channelListener.caught(tcpChannel, new Exception(cause));
        } else {
            channelListener.caught(tcpChannel, cause);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        //fixme 检测是否入队成功
        boolean queued = queuedWrites.offer(new WriteOperation((ByteBuf) msg, promise));
        Netty4TcpChannel tcpChannel = ctx.channel().attr(Netty4Transport.TCP_CHANNEL_KEY).get();
        promise.addListener(future -> {
            ChannelListener channelListener = transport.getChannelListener();
            if (future.isSuccess()) {
                channelListener.sent(tcpChannel, msg);
            } else {
                Throwable cause = future.cause();
                if (cause instanceof Error) {
                    channelListener.caught(tcpChannel, new Exception(cause));
                } else {
                    channelListener.caught(tcpChannel, cause);
                }
            }
        });

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        if (ctx.channel().isWritable()) {
            doFlush(ctx);
        }
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void flush(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        if (channel.isWritable() || channel.isActive() == false) {
            doFlush(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        doFlush(ctx);
        Netty4TcpChannel tcpChannel = ctx.channel().attr(Netty4Transport.TCP_CHANNEL_KEY).get();
        transport.getChannelListener().disconnected(tcpChannel);
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Netty4TcpChannel tcpChannel = ctx.channel().attr(Netty4Transport.TCP_CHANNEL_KEY).get();
        transport.getChannelListener().connected(tcpChannel);
        super.channelActive(ctx);
    }


    private void doFlush(ChannelHandlerContext ctx) {
        assert ctx.executor().inEventLoop();
        final Channel channel = ctx.channel();
        if (channel.isActive() == false) {
            if (currentWrite != null) {
                currentWrite.promise.tryFailure(new ClosedChannelException());
            }
            failQueuedWrites();
            return;
        }
        while (channel.isWritable()) {
            if (currentWrite == null) {
                currentWrite = queuedWrites.poll();
            }
            if (currentWrite == null) {
                break;
            }
            final WriteOperation write = currentWrite;
            if (write.buf.readableBytes() == 0) {
                write.promise.trySuccess();
                currentWrite = null;
                continue;
            }
            final int readableBytes = write.buf.readableBytes();
            final int bufferSize = Math.min(readableBytes, 1 << 18);
            final int readerIndex = write.buf.readerIndex();
            final boolean sliced = readableBytes != bufferSize;
            final ByteBuf writeBuffer;
            if (sliced) {
                writeBuffer = write.buf.retainedSlice(readerIndex, bufferSize);
                write.buf.readerIndex(readerIndex + bufferSize);
            } else {
                writeBuffer = write.buf;
            }
            final ChannelFuture writeFuture = ctx.write(writeBuffer);
            if (sliced == false || write.buf.readableBytes() == 0) {
                currentWrite = null;
                writeFuture.addListener(future -> {
                    assert ctx.executor().inEventLoop();
                    if (future.isSuccess()) {
                        write.promise.trySuccess();
                    } else {
                        write.promise.tryFailure(future.cause());
                    }
                });
            } else {
                writeFuture.addListener(future -> {
                    assert ctx.executor().inEventLoop();
                    if (future.isSuccess() == false) {
                        write.promise.tryFailure(future.cause());
                    }
                });
            }
            ctx.flush();
            if (channel.isActive() == false) {
                failQueuedWrites();
                return;
            }
        }
    }

    private void failQueuedWrites() {
        WriteOperation queuedWrite;
        while ((queuedWrite = queuedWrites.poll()) != null) {
            queuedWrite.promise.tryFailure(new ClosedChannelException());
        }
    }

    private static final class WriteOperation {
        private final ByteBuf buf;
        private final ChannelPromise promise;

        WriteOperation(ByteBuf buf, ChannelPromise promise) {
            this.buf = buf;
            this.promise = promise;
        }
    }
}