package cn.sliew.milky.remote;

import cn.sliew.milky.remote.transport.TcpChannel;

public interface TransportRequestHandler<Request extends TransportRequest> {

    void messageReceived(Request request, TcpChannel channel) throws Exception;
}
