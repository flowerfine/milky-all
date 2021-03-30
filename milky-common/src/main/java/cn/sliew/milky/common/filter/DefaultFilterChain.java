package cn.sliew.milky.common.filter;

import cn.sliew.milky.log.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultFilterChain<Request, Response> implements ActionFilterChain<Request, Response> {

    private final AtomicInteger index = new AtomicInteger();
    private final ActionFilter<Request, Response>[] filters;
    private final Logger logger;

    public DefaultFilterChain(List<ActionFilter<Request, Response>> filters, Logger logger) {
        this.filters = (ActionFilter<Request, Response>[]) filters.stream().toArray();
        this.logger = logger;
    }

    @Override
    public void proceed(Request request, ActionListener<Response> listener) {
        int i = index.getAndIncrement();
        try {
            if (i < this.filters.length) {
                this.filters[i].apply(request, listener, this);
            } else {
                listener.onFailure(new IllegalStateException("proceed was called too many times"));
            }
        } catch (Exception e) {
            logger.trace("Error during transport action execution.", e);
            listener.onFailure(e);
        }
    }
}