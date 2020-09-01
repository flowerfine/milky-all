package cn.sliew.milky.transport;

import cn.sliew.milky.transport.exchange.TransportRequest;

import java.io.Closeable;

/**
 * 是channel的封装，包含多个channel
 */
public interface Connection extends Closeable {

    TcpChannelSelectStrategy selectStrategy();

    TcpChannel channel();

    /**
     * Sends the request to the node this connection is associated with
     *
     * @param requestId
     * @param request   the request to send
     */
    void sendRequest(long requestId, TransportRequest request);

    boolean isClosed();

    @Override
    void close();
}
