package cn.sliew.milky.concurrent;

/**
 * The {@link CompleteFuture} which is succeeded already.
 */
public class SucceededFuture<V> extends CompleteFuture<V> {

    private final V result;

    public SucceededFuture(V result) {
        this.result = result;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public V getNow() {
        return result;
    }
}
