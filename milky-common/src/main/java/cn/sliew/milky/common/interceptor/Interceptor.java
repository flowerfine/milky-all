package cn.sliew.milky.common.interceptor;

import java.io.Serializable;

/**
 * The Interceptor allows to intercept the invocation.
 * <p>
 * This is useful to provide logging, fallback, exception, transform or substitute values.
 * <p>
 * fixme If multiple interceptors are registered with the same priority, then their execution order
 *      may be non deterministic.
 */
public interface Interceptor<Request, Response> extends Serializable {

    /**
     * Intercept the invocation and return the corresponding response or a custom response built by the interceptor.
     * Calling {@link InterceptorContext#proceed(java.lang.Object)} will continue to execute the interceptor chain.
     * The chain can be short-circuited by returning another instance of response
     *
     * @param request the request being intercepted.
     * @param context the interceptor context.
     */
    Response intercept(Request request, InterceptorContext<Request, Response> context);

    Interceptor EMPTY = new Interceptor() {
        private static final long serialVersionUID = -9169967347679239961L;

        @Override
        public Object intercept(Object o, InterceptorContext context) {
            return null;
        }
    };

}
