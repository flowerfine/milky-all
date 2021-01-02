package cn.sliew.milky.transport;

import cn.sliew.milky.transport.exchange.ResponseContext;
import cn.sliew.milky.transport.exchange.TransportRequest;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DelegatingTransportMessageListener implements TransportMessageListener {

    final List<TransportMessageListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void onRequestSent(long requestId, TransportRequest request) {
        for (TransportMessageListener listener : listeners) {
            listener.onRequestSent(requestId, request);
        }
    }

    @Override
    public void onRequestReceived(long requestId) {
        for (TransportMessageListener listener : listeners) {
            listener.onRequestReceived(requestId);
        }
    }

    @Override
    public void onResponseSent(long requestId, String action, Exception error) {
        for (TransportMessageListener listener : listeners) {
            listener.onResponseSent(requestId, action, error);
        }
    }

    @Override
    public void onResponseSent(long requestId, TransportResponse response) {
        for (TransportMessageListener listener : listeners) {
            listener.onResponseSent(requestId, response);
        }
    }

    @Override
    public void onResponseReceived(long requestId, ResponseContext holder) {
        for (TransportMessageListener listener : listeners) {
            listener.onResponseReceived(requestId, holder);
        }
    }
}