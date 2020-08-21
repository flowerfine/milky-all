package cn.sliew.milky.common.watchdog;

/**
 * Protects against long running operations that happen between the register and unregister invocations.
 * eg. Threads that invoke {@link #register()}, but take too long to invoke the {@link #unregister()} method
 * will be interrupted.
 */
public interface ThreadWatchdog {

    /**
     * Registers the current thread and interrupts the current thread
     * if the takes too long for this thread to invoke {@link #unregister()}.
     */
    void register();

    /**
     * @return The maximum allowed time in milliseconds for a thread to invoke {@link #unregister()}
     * after {@link #register()} has been invoked before this ThreadWatchDog starts to interrupting that thread.
     */
    long maxExecutionTimeInMillis();

    /**
     * Unregisters the current thread and prevents it from being interrupted.
     */
    void unregister();
}
