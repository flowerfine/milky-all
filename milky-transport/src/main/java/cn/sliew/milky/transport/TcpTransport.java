package cn.sliew.milky.transport;

import cn.sliew.milky.transport.exchange.TransportMessageListener;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface TcpTransport {

    /**
     * The address the transport is bound on.
     * transport绑定的地址
     */
    InetSocketAddress boundAddress();

    /**
     * Opens a new connection to the given node.
     */
    Connection connect(Node node, ActionListener<Connection> listener);

    void bind(InetAddress hostAddress, String port);


    void addMessageListener(TransportMessageListener listener);

    boolean removeMessageListener(TransportMessageListener listener);
}
