package cn.sliew.milky.common.concurrent;

import java.util.concurrent.Callable;

public interface CallableWrapper<T> extends Callable<T> {

    @Override
    default T call() throws Exception {
        T result = null;
        try {
            onBefore();
            result = doCall();
            onAfter(result);
        } catch (Exception t) {
            onFailure(t);
        } finally {
            onFinal();
        }
        return result;
    }

    /**
     * This method has the same semantics as {@link Runnable#run()}
     *
     * @throws InterruptedException if the run method throws an InterruptedException
     */
    T doCall() throws Exception;

    /**
     * This method is invoked for all exception thrown by {@link #doCall()}
     */
    void onFailure(Exception e);

    /**
     * This method is called before all execution for init.
     */
    default void onBefore() throws Exception {

    }

    /**
     * This method is called after execution.
     */
    default void onAfter(T result) throws Exception {

    }

    /**
     * This method is called in a finally block after successful execution
     * or on a rejection.
     */
    default void onFinal() {
        // nothing by default
    }
}
