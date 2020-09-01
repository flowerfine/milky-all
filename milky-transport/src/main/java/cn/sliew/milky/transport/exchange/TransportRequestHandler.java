package cn.sliew.milky.transport.exchange;

import cn.sliew.milky.transport.Connection;

public interface TransportRequestHandler<T extends TransportRequest> {

    void messageReceived(T request, Connection connection);
}