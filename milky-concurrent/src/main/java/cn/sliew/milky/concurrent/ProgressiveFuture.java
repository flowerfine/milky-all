package cn.sliew.milky.concurrent;

/**
 * A {@link Future} which is used to indicate the progress of an operation.
 */
public interface ProgressiveFuture<V> extends Future<V> {

    @Override
    ProgressiveFuture<V> addListener(FutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressiveFuture<V> removeListener(FutureListener<? extends Future<? super V>> listener);

    @Override
    ProgressiveFuture<V> sync() throws InterruptedException;

    @Override
    ProgressiveFuture<V> await() throws InterruptedException;
}
