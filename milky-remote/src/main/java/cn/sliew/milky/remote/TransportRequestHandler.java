package cn.sliew.milky.remote;

import cn.sliew.milky.remote.transport.TcpChannel;

public interface TransportRequestHandler<T extends TransportRequest> {

    void messageReceived(T request, TcpChannel channel) throws Exception;
}
