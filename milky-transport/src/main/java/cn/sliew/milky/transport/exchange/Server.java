package cn.sliew.milky.transport.exchange;

import cn.sliew.milky.transport.TcpChannel;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface Server extends TcpChannel {

    /**
     * is bound.
     *
     * @return bound
     */
    boolean isBound();

    /**
     * bind to server.
     *
     * @param port bind port
     */
    void bind(int port);

    /**
     * get channels.
     *
     * @return channels
     */
    Collection<TcpChannel> getChannels();

    /**
     * get channel.
     *
     * @param remoteAddress accepted remote address
     * @return channel
     */
    TcpChannel getChannel(InetSocketAddress remoteAddress);
}
