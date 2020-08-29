package cn.sliew.milky.transport;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface Server extends Channel {

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
    Collection<Channel> getChannels();

    /**
     * get channel.
     *
     * @param remoteAddress accepted remote address
     * @return channel
     */
    Channel getChannel(InetSocketAddress remoteAddress);
}
