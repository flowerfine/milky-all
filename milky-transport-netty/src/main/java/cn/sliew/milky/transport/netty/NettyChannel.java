package cn.sliew.milky.transport.netty;

import cn.sliew.milky.concurrent.CompletableContext;
import cn.sliew.milky.transport.ActionListener;
import cn.sliew.milky.transport.TcpChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;

public class NettyChannel implements TcpChannel {

    private final Channel channel;
    private final boolean isServer;
    private final CompletableContext<Void> connectContext = new CompletableContext<>();
    private final CompletableContext<Void> closeContext = new CompletableContext<>();

    NettyChannel(Channel channel, boolean isServer, ChannelFuture channelFuture) {
        this.channel = channel;
        this.isServer = isServer;
        this.channel.closeFuture().addListener(f -> {
            if (f.isSuccess()) {
                closeContext.complete(null);
            } else {
                Throwable cause = f.cause();
                if (cause instanceof Error) {
                    closeContext.completeExceptionally(new Exception(cause));
                } else {
                    closeContext.completeExceptionally((Exception) cause);
                }
            }
        });

        channelFuture.addListener(f -> {
            if (f.isSuccess()) {
                connectContext.complete(null);
            } else {
                Throwable cause = f.cause();
                if (cause instanceof Error) {
                    connectContext.completeExceptionally(new Exception(cause));
                } else {
                    connectContext.completeExceptionally((Exception) cause);
                }
            }
        });
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
    public void sendMessage(Object message, ActionListener<Void> listener) {
        ChannelPromise writePromise = channel.newPromise();
        writePromise.addListener(f -> {
            if (f.isSuccess()) {
                listener.onResponse(null);
            } else {
                final Throwable cause = f.cause();
                if (cause instanceof Error) {
                    listener.onFailure(new Exception(cause));
                } else {
                    listener.onFailure((Exception) cause);
                }
            }
        });
        channel.writeAndFlush(message, writePromise);

        if (channel.eventLoop().isShutdown()) {
            listener.onFailure(new RuntimeException("Cannot send message, event loop is shutting down."));
        }
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() {
        channel.close();
    }
}