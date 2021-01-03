package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.TransportResponse;
import cn.sliew.milky.remote.TransportResponseHandler;

public class ResponseContext<T extends TransportResponse> {

    private final TransportResponseHandler<T> handler;
    private final String action;

    public ResponseContext(TransportResponseHandler<T> handler, String action) {
        this.handler = handler;
        this.action = action;
    }
}
