package cn.sliew.milky.common.interceptor;

import java.io.Serializable;

/**
 * Exposes contextual information about the intercepted invocation of {@link Interceptor}. This allows
 * implementers to control the behavior of the invocation chain.
 */
public interface InterceptorContext<Reuqest, Response> extends Serializable {

    /**
     * Proceeds to the next interceptor in the chain.
     *
     * @param reuqest the request.
     */
    Response proceed(Reuqest reuqest);
}
