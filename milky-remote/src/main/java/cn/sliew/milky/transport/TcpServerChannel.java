package cn.sliew.milky.transport;

import java.net.InetSocketAddress;

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
}
