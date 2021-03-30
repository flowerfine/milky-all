package cn.sliew.milky.remote.netty4;

import cn.sliew.milky.concurrent.CompletableContext;
import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import cn.sliew.milky.remote.transport.AbstractChannel;
import cn.sliew.milky.remote.transport.ActionListener;
import cn.sliew.milky.remote.transport.TcpChannel;
import cn.sliew.milky.remote.transport.TcpServerChannel;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;

public class Netty4TcpServerChannel extends AbstractChannel implements TcpServerChannel {

    private static final Logger logger = LoggerFactory.getLogger(Netty4Transport.class);

    private final Channel channel;
    private final Map<InetSocketAddress, TcpChannel> acceptedChannels = Maps.newConcurrentMap();
    private final CompletableContext<Void> closeContext;

    Netty4TcpServerChannel(Channel channel) {
        this.channel = channel;
        this.closeContext = new CompletableContext<>();
        Netty4TcpChannel.addListener(this.channel.closeFuture(), closeContext);
    }


    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public Collection<TcpChannel> getChannels() {
        return acceptedChannels.values();
    }

    @Override
    public TcpChannel getChannel(InetSocketAddress remoteAddress) {
        return acceptedChannels.get(remoteAddress);
    }

    protected void serverAcceptedChannel(TcpChannel channel) {
        acceptedChannels.put(channel.getRemoteAddress(), channel);
        channel.addCloseListener(ActionListener.wrap(() -> acceptedChannels.remove(channel)));
        logger.trace("Tcp transport channel accepted: {}", channel);
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
