package cn.sliew.milky.remote.transport;

import java.net.InetSocketAddress;

public interface Transport {

    TcpChannel connnect(Node node, ChannelListener listener);

    TcpServerChannel bind(InetSocketAddress address, ChannelListener listener);
}
