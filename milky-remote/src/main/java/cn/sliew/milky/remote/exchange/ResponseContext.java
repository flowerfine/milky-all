package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.TransportResponse;
import cn.sliew.milky.remote.TransportResponseHandler;

public class ResponseContext<Response extends TransportResponse> {

    private final TransportResponseHandler<Response> handler;
    private final Connection connection;
    private final String action;

    public ResponseContext(TransportResponseHandler<Response> handler, Connection connection, String action) {
        this.handler = handler;
        this.connection = connection;
        this.action = action;
    }

    public TransportResponseHandler<Response> handler() {
        return handler;
    }

    public Connection connection() {
        return connection;
    }

    public String action() {
        return action;
    }
}
