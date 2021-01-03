package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.TransportRequest;
import cn.sliew.milky.remote.TransportRequestHandler;
import cn.sliew.milky.remote.transport.TcpChannel;

public class RequestContext<Request extends TransportRequest> {

    private final String action;
    private final TransportRequestHandler<Request> handler;
    private final String executor;

    public RequestContext(String action, TransportRequestHandler<Request> handler, String executor) {
        this.action = action;
        this.handler = handler;
        this.executor = executor;
    }

    public void messageReceived(Request request, TcpChannel channel) throws Exception {
        handler.messageReceived(request, channel);
    }

    public String action() {
        return action;
    }
}
