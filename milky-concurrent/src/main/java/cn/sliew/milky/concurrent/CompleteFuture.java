package cn.sliew.milky.concurrent;

/**
 * A skeletal {@link Future} implementation which represents a {@link Future}
 * which has been completed already.
 */
public abstract class CompleteFuture<V> extends AbstractFuture<V> {

    @Override
    public Future<V> addListener(FutureListener<? extends Future<? super V>> listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        notifyListener0(this, listener);
        return this;
    }

    @Override
    public Future<V> removeListener(FutureListener<? extends Future<? super V>> listener) {
        // NOOP
        return this;
    }

    private void notifyListener0(Future<V> future, FutureListener<? extends Future<? super V>> listener) {
        try {
            listener.onComplete(future);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
