package cn.sliew.milky.common.util;

import static cn.sliew.milky.common.util.ExceptionUtil.throwAsUncheckedException;

public final class ThrowableUtil {

    private ThrowableUtil() {
        throw new IllegalStateException("can't do this!");
    }

    /**
     * Rethrow the supplied {@link Throwable exception} if it is
     * <em>unrecoverable</em>.
     *
     * <p>If the supplied {@code exception} is not <em>unrecoverable</em>, this
     * method does nothing.
     */
    public static void rethrowIfUnrecoverable(Throwable exception) {
        if (exception instanceof OutOfMemoryError) {
            throwAsUncheckedException(exception);
        }
    }
}
