package cn.sliew.milky.remote.netty4;

import cn.sliew.milky.concurrent.CompletableContext;
import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import cn.sliew.milky.remote.transport.AbstractChannel;
import cn.sliew.milky.remote.transport.ActionListener;
import cn.sliew.milky.remote.transport.ChannelHandler;
import cn.sliew.milky.remote.transport.TcpChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;

public class Netty4TcpChannel extends AbstractChannel implements TcpChannel {

    private static final Logger logger = LoggerFactory.getLogger(Netty4Transport.class);

    private final Channel channel;
    private final boolean isServer;
    private final CompletableContext<Void> connectContext;
    private final CompletableContext<Void> closeContext;

    Netty4TcpChannel(Channel channel, boolean isServer, ChannelFuture connectFuture) {
        this.channel = channel;
        this.isServer = isServer;
        this.connectContext = new CompletableContext<>();
        this.closeContext = new CompletableContext<>();
        addListener(this.channel.closeFuture(), closeContext);
        addListener(connectFuture, connectContext);
    }

    /**
     * Adds a listener that completes the given {@link CompletableContext} to the given {@link ChannelFuture}.
     *
     * @param channelFuture Channel future
     * @param context       Context to complete
     */
    static void addListener(ChannelFuture channelFuture, CompletableContext<Void> context) {
        channelFuture.addListener(f -> {
            if (f.isSuccess()) {
                context.complete(null);
            } else {
                Throwable cause = f.cause();
                if (cause instanceof Error) {
                    context.completeExceptionally(new Exception(cause));
                } else {
                    context.completeExceptionally((Exception) cause);
                }
            }
        });
    }

    @Override
    public boolean isServerChannel() {
        return isServer;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public void sendMessage(byte[] message) {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.heapBuffer(message.length).writeBytes(message);
        ChannelPromise promise = channel.newPromise();
        //todo promise回掉设置
        channel.writeAndFlush(byteBuf, promise);
    }

    @Override
    public void registerListener(ChannelHandler<TcpChannel> listener) {
        connectContext.addListener((avoid, exception) -> {
            if (exception != null) {
                listener.caught(this, exception);
            } else {
                listener.connected(this);
            }
        });

    }

    @Override
    public void close() {
        super.close();
        channel.close();
    }

    @Override
    public void addCloseListener(ActionListener<Void> listener) {
        closeContext.addListener((avoid, exception) -> {
            if (exception != null) {
                listener.onFailure(exception);
            } else {
                listener.onResponse(avoid);
            }
        });
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }
}