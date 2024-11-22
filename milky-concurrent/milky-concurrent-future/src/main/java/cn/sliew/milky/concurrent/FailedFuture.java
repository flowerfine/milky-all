package cn.sliew.milky.concurrent;

/**
 * The {@link CompleteFuture} which is failed already.
 */
public class FailedFuture<V> extends CompleteFuture<V> {

    private final Throwable cause;

    public FailedFuture(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        this.cause = cause;
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        throwException0(cause);
        return this;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public V getNow() {
        return null;
    }

    static <E extends Throwable> void throwException0(Throwable t) throws E {
        throw (E) t;
    }
}
