package cn.sliew.milky.transport;

public class RequestContext<Request extends TransportRequest> {

    private final TransportRequestHandler<Request> handler;

    public RequestContext(TransportRequestHandler<Request> handler) {
        this.handler = handler;
    }

    public Request newRequest() {
        return null;
    }

    public void processMessageReceived(Request request, Connection connection) {
        handler.messageReceived(request, connection);
    }
}
