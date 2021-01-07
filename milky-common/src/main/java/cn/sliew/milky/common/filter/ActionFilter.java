package cn.sliew.milky.common.filter;

/**
 * A filter allowing to filter transport actions
 */
public interface ActionFilter<Request, Response> {

    /**
     * The position of the filter in the chain. Execution is done from lowest order to highest.
     */
    int order();

    /**
     * Enables filtering the execution of an action on the request side, either by sending a response through the
     * {@link ActionListener} or by continuing the execution through the given {@link ActionFilterChain chain}
     */
    void apply(Request request, ActionListener<Response> listener, ActionFilterChain<Request, Response> chain);
}
