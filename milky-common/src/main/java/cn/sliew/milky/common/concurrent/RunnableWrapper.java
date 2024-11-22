package cn.sliew.milky.common.concurrent;

import java.util.concurrent.RejectedExecutionException;

public interface RunnableWrapper extends Runnable {

    @Override
    default void run() {
        try {
            onBefore();
            doRun();
            onAfter();
        } catch (RejectedExecutionException t) {
            onRejection(t);
        }  catch (Exception t) {
            onFailure(t);
        } finally {
            onFinal();
        }
    }

    /**
     * This method has the same semantics as {@link Runnable#run()}
     *
     * @throws InterruptedException if the run method throws an InterruptedException
     */
    void doRun() throws Exception;

    /**
     * This method is invoked for all exception thrown by {@link #doRun()}
     */
    void onFailure(Exception e);

    /**
     * This should be executed if the thread-pool executing this action rejected the execution.
     * The default implementation forwards to {@link #onFailure(Exception)}
     */
    default void onRejection(Exception e) {
        onFailure(e);
    }

    /**
     * This method is called before all execution for init.
     */
    default void onBefore() throws Exception {

    }

    /**
     * This method is called after execution.
     */
    default void onAfter() throws Exception {

    }

    /**
     * This method is called in a finally block after successful execution
     * or on a rejection.
     */
    default void onFinal() {
        // nothing by default
    }

    /**
     * Should the runnable force its execution in case it gets rejected?
     */
    default boolean isForceExecution() {
        return false;
    }
}
