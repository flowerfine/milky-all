package cn.sliew.milky.concurrent;

import java.util.concurrent.TimeUnit;

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

    private void notifyListener0(Future future, FutureListener listener) {
        try {
            listener.onComplete(future);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public Future<V> sync() throws InterruptedException {
        return this;
    }

    public Future<V> await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return this;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }
}
