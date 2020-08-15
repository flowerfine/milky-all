package cn.sliew.milky.common.filter;

/**
 * A filter chain allowing to continue and process the transport action request
 * <p>
 * 参考elasticsearch的过滤链实现
 */
public interface ActionFilterChain<Request, Response> {

    /**
     * Continue processing the request. Should only be called if a response has not been sent through
     * the given {@link ActionListener listener}
     */
    void proceed(Request request, ActionListener<Response> listener);
}
