package cn.sliew.milky.common.concurrent;

public interface RunnableWrapper extends Runnable {

    @Override
    default void run() {
        try {
            onBefore();
            doRun();
            onAfter();
        } catch (Exception t) {
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
}
