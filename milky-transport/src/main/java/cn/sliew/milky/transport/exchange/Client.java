package cn.sliew.milky.transport.exchange;

import cn.sliew.milky.transport.TcpChannel;

import java.net.InetSocketAddress;

public interface Client extends TcpChannel {

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
