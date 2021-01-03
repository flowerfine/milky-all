package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.RemoteService;
import cn.sliew.milky.remote.TransportRequest;
import cn.sliew.milky.remote.TransportResponse;
import cn.sliew.milky.remote.TransportResponseHandler;
import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ResponseHandlerRegistry {

    private final ConcurrentMap<Long, ResponseContext<? extends TransportResponse>> handlers = Maps.newConcurrentMap();
    private final AtomicLong requestIdGenerator = new AtomicLong();

    /**
     * Returns <code>true</code> if the give request ID has a context associated with it.
     */
    public boolean contains(long requestId) {
        return handlers.containsKey(requestId);
    }

    /**
     * Removes and return the {@link ResponseContext} for the given request ID or returns
     * <code>null</code> if no context is associated with this request ID.
     */
    public ResponseContext<? extends TransportResponse> remove(long requestId) {
        return handlers.remove(requestId);
    }

    /**
     * Adds a new response context and associates it with a new request ID.
     *
     * @return the new request ID
     * @see Connection#sendRequest(long, String, TransportRequest)
     */
    public long add(ResponseContext<? extends TransportResponse> holder) {
        long requestId = newRequestId();
        ResponseContext<? extends TransportResponse> existing = handlers.put(requestId, holder);
        assert existing == null : "request ID already in use: " + requestId;
        return requestId;
    }

    /**
     * Returns a new request ID to use when sending a message via {@link Connection#sendRequest(long, String, TransportRequest)}
     */
    long newRequestId() {
        return requestIdGenerator.incrementAndGet();
    }

    /**
     * called by the {@link RemoteService} implementation when a response or an exception has been received for a previously
     * sent request (before any processing or deserialization was done). Returns the appropriate response handler or null if not
     * found.
     */
    public TransportResponseHandler<? extends TransportResponse> onResponseReceived(long requestId, MessageListener listener) {
        ResponseContext<? extends TransportResponse> context = handlers.remove(requestId);
        listener.onResponseReceived(requestId, context);
        if (context == null) {
            return null;
        } else {
            return context.handler();
        }
    }
}
