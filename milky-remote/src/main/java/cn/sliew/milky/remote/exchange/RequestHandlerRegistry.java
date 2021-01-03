package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.TransportRequest;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestHandlerRegistry {

    private volatile Map<String, RequestContext<? extends TransportRequest>> requestHandlers = Collections.emptyMap();

    synchronized <Request extends TransportRequest> void registerHandler(RequestContext<Request> requestContext) {
        if (requestHandlers.containsKey(requestContext.action())) {
            throw new IllegalArgumentException("transport handlers for action " + requestContext.action() + " is already registered");
        }
        HashMap<String, RequestContext<? extends TransportRequest>> hashMap = Maps.newHashMap(requestHandlers);
        hashMap.put(requestContext.action(), requestContext);
        requestHandlers = Collections.unmodifiableMap(hashMap);
    }

    public <Request extends TransportRequest> RequestContext<Request> getHandler(String action) {
        return (RequestContext<Request>) requestHandlers.get(action);
    }
}
