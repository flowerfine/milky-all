package cn.sliew.milky.concurrent;

import java.util.EventListener;

/**
 * Listens to the result of a {@link BaseFuture}.  The result of the asynchronous operation
 * is notified once this listener is added by calling {@link BaseFuture#addListener(FutureListener)}.
 */
public interface FutureListener<F extends BaseFuture<?>> extends EventListener {

    /**
     * Invoked when the operation associated with the {@link BaseFuture} has been completed.
     *
     * @param future the source {@link BaseFuture} which called this callback
     */
    void onComplete(F future) throws Exception;
}
