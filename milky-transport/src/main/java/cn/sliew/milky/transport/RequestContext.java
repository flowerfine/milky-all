package cn.sliew.milky.transport;

public class RequestContext<Request extends TransportRequest> {

    private final Connection connection;

    private final TransportRequestHandler<Request> handler;

    public RequestContext(TransportRequestHandler<Request> handler, Connection connection) {
        this.handler = handler;
        this.connection = connection;
    }

    public Request newRequest() {
        return null;
    }

    public void processMessageReceived(Request request) {
        handler.messageReceived(request, connection);
    }


    public TransportRequestHandler<Request> handler() {
        return handler;
    }

    public Connection connection() {
        return this.connection;
    }
}
