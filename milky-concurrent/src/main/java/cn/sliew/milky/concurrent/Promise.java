package cn.sliew.milky.concurrent;

/**
 * Special {@link Future} which is writable.
 */
public interface Promise<V> extends Future<V> {

    /**
     * Marks this future as a success and notifies all
     * listeners.
     * <p>
     * If it is success or failed already it will throw an {@link IllegalStateException}.
     */
    Promise<V> setSuccess(V result);

    /**
     * Marks this future as a success and notifies all
     * listeners.
     *
     * @return {@code true} if and only if successfully marked this future as
     * a success. Otherwise {@code false} because this future is
     * already marked as either a success or a failure.
     */
    boolean trySuccess(V result);

    /**
     * Marks this future as a failure and notifies all
     * listeners.
     * <p>
     * If it is success or failed already it will throw an {@link IllegalStateException}.
     */
    Promise<V> setFailure(Throwable cause);

    /**
     * Marks this future as a failure and notifies all
     * listeners.
     *
     * @return {@code true} if and only if successfully marked this future as
     * a failure. Otherwise {@code false} because this future is
     * already marked as either a success or a failure.
     */
    boolean tryFailure(Throwable cause);

    @Override
    Promise<V> addListener(FutureListener<? extends Future<? super V>> listener);

    @Override
    Promise<V> removeListener(FutureListener<? extends Future<? super V>> listener);

    @Override
    Promise<V> sync() throws InterruptedException;

    @Override
    Promise<V> await() throws InterruptedException;
}

