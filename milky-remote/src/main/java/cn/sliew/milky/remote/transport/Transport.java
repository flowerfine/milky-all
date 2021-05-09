package cn.sliew.milky.remote.transport;

import java.net.InetSocketAddress;

public interface Transport {

    TcpChannel connnect(Node node, ChannelHandler listener);

    TcpServerChannel bind(InetSocketAddress address, ChannelHandler listener);
}
