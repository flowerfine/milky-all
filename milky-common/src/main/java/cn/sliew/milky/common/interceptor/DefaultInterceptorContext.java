package cn.sliew.milky.common.interceptor;

/**
 * Interceptor will be registered by linked node chain and interception will be applied orderly.
 */
public class DefaultInterceptorContext<Request, Response> implements InterceptorContext<Request, Response> {

    private static final long serialVersionUID = -8969967347689639974L;

    private final Interceptor<Request, Response> interceptor;
    private final InterceptorContext<Request, Response> next;

    public DefaultInterceptorContext(Interceptor<Request, Response> interceptor,
                                     InterceptorContext<Request, Response> next) {
        this.interceptor = interceptor;
        this.next = next;
    }

    @Override
    public Response proceed(Request request) {
        return interceptor.intercept(request, next);
    }

}
