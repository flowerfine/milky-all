package cn.sliew.milky.transport;

public class ResponseContext<Response extends TransportResponse> {

    private final Connection connection;

    private final TransportResponseHandler<Response> handler;

    ResponseContext(TransportResponseHandler<Response> handler, Connection connection) {
        this.handler = handler;
        this.connection = connection;
    }

    public Response newResponse() {
        return null;
    }

    public void processResponse(Response request) {
        handler.handleResponse(request);
    }

    public void processException(Exception exp) {
        handler.handleException(exp);
    }

    public TransportResponseHandler<Response> handler() {
        return handler;
    }

    public Connection connection() {
        return this.connection;
    }
}
