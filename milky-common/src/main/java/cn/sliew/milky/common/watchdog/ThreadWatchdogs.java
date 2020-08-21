package cn.sliew.milky.common.watchdog;

import java.util.concurrent.ScheduledFuture;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;

public enum ThreadWatchdogs {
    ;

    /**
     * Returns an implementation that checks for each fixed interval if there are threads that have invoked {@link ThreadWatchdog#register()}
     * and not {@link ThreadWatchdog#unregister()} and have been in this state for longer than the specified max execution interval and
     * then interrupts these threads.
     *
     * @param interval             The fixed interval to check if there are threads to interrupt
     * @param maxExecutionTime     The time a thread has the execute an operation.
     * @param relativeTimeSupplier A supplier that returns relative time
     * @param scheduler            A scheduler that is able to execute a command for each fixed interval
     */
    public ThreadWatchdog newInstance(long interval,
                                      long maxExecutionTime,
                                      LongSupplier relativeTimeSupplier,
                                      BiFunction<Long, Runnable, ScheduledFuture<?>> scheduler) {
        return new DefaultThreadWatchdog(interval, maxExecutionTime, relativeTimeSupplier, scheduler);
    }

    /**
     * @return A noop implementation that does not interrupt threads and is useful for testing and pre-defined grok expressions.
     */
    public ThreadWatchdog noop() {
        return Noop.INSTANCE;
    }
}
