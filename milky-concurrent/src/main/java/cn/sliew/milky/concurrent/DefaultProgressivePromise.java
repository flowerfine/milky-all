package cn.sliew.milky.concurrent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DefaultProgressivePromise<V> extends DefaultPromise<V> implements ProgressivePromise<V> {

    @Override
    public ProgressivePromise<V> setProgress(long progress, long total) {
        if (total < 0) {
            // total unknown
            total = -1; // normalize
            if (progress < 0) {
                throw new IllegalArgumentException("progress: " + progress + " (expected: >= 0)");
            }
        } else if (progress < 0 || progress > total) {
            throw new IllegalArgumentException(
                    "progress: " + progress + " (expected: 0 <= progress <= total (" + total + "))");
        }

        if (isDone()) {
            throw new IllegalStateException("complete already");
        }

        notifyProgressiveListeners(progress, total);
        return this;
    }

    @Override
    public boolean tryProgress(long progress, long total) {
        if (total < 0) {
            total = -1;
            if (progress < 0 || isDone()) {
                return false;
            }
        } else if (progress < 0 || progress > total || isDone()) {
            return false;
        }

        notifyProgressiveListeners(progress, total);
        return true;
    }

    void notifyProgressiveListeners(long progress, long total) {
        List<ProgressiveFutureListener> listeners = progressiveListeners();
        if (listeners.isEmpty()) {
            return;
        }
        listeners.forEach(listener -> notifyProgressiveListener0(this, listener, progress, total));
    }

    private synchronized List<ProgressiveFutureListener> progressiveListeners() {
        List<FutureListener<? extends Future<? super V>>> listeners = this.listeners;
        if (listeners == null || listeners.isEmpty()) {
            // No listeners added
            return Collections.emptyList();
        }
        List<ProgressiveFutureListener> progressiveFutureListeners = new LinkedList<>();
        for (FutureListener listener : listeners) {
            if (listener instanceof ProgressiveFutureListener) {
                progressiveFutureListeners.add((ProgressiveFutureListener) listener);
            }
        }
        return progressiveFutureListeners;
    }

    private static void notifyProgressiveListener0(
            ProgressiveFuture future, ProgressiveFutureListener listener, long progress, long total) {
        try {
            listener.onProgressed(future, progress, total);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public ProgressivePromise<V> setSuccess(V result) {
        super.setSuccess(result);
        return this;
    }

    @Override
    public ProgressivePromise<V> setFailure(Throwable cause) {
        super.setFailure(cause);
        return this;
    }

    @Override
    public ProgressivePromise<V> addListener(FutureListener<? extends Future<? super V>> listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    public ProgressivePromise<V> removeListener(FutureListener<? extends Future<? super V>> listener) {
        super.removeListener(listener);
        return this;
    }

    @Override
    public ProgressivePromise<V> sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ProgressivePromise<V> await() throws InterruptedException {
        super.await();
        return this;
    }
}
