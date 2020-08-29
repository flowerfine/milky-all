package cn.sliew.milky.transport;

import java.net.InetSocketAddress;

public interface Client extends Channel {

    /**
     * is connected.
     *
     * @return connected
     */
    boolean isConnected();

    /**
     * connect.
     *
     * @param remoteAddress remote server address.
     */
    void connect(InetSocketAddress remoteAddress);
}
