package cn.sliew.milky.transport;

public interface TransportRequestHandler<T extends TransportRequest> {

    void messageReceived(T request, Connection connection);
}