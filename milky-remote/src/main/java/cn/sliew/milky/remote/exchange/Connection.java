package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.TransportRequest;
import cn.sliew.milky.remote.transport.TcpChannel;

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
    void sendRequest(long requestId, String action, TransportRequest request);

    boolean isClosed();

    @Override
    void close();
}