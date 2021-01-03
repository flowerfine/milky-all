package cn.sliew.milky.remote.transport;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * tcp的server
 * <p>
 * 交给netty进行实现了
 */
public interface TcpServerChannel extends Channel {

    /**
     * Returns the local address for this channel.
     *
     * @return the local address of this channel.
     */
    InetSocketAddress getLocalAddress();

    /**
     * get channels.
     *
     * @return channels
     */
    Collection<TcpChannel> getChannels();

    /**
     * get channel.
     *
     * @param remoteAddress
     * @return channel
     */
    TcpChannel getChannel(InetSocketAddress remoteAddress);
}
