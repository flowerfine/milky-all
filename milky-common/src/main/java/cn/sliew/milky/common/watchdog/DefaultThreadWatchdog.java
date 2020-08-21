package cn.sliew.milky.common.watchdog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;

public class DefaultThreadWatchdog implements ThreadWatchdog {

    private final long interval;
    private final long maxExecutionTime;
    private final LongSupplier relativeTimeSupplier;
    private final BiFunction<Long, Runnable, ScheduledFuture<?>> scheduler;
    private final AtomicInteger registered = new AtomicInteger(0);
    private final AtomicBoolean running = new AtomicBoolean(false);
    final ConcurrentHashMap<Thread, Long> registry = new ConcurrentHashMap<>();

    DefaultThreadWatchdog(long interval,
                          long maxExecutionTime,
                          LongSupplier relativeTimeSupplier,
                          BiFunction<Long, Runnable, ScheduledFuture<?>> scheduler) {
        this.interval = interval;
        this.maxExecutionTime = maxExecutionTime;
        this.relativeTimeSupplier = relativeTimeSupplier;
        this.scheduler = scheduler;
    }

    public void register() {
        registered.getAndIncrement();
        Long previousValue = registry.put(Thread.currentThread(), relativeTimeSupplier.getAsLong());
        if (previousValue != null) {
            throw new NullPointerException();
        }
        if (running.compareAndSet(false, true) == true) {
            scheduler.apply(interval, this::interruptLongRunningExecutions);
        }
    }

    @Override
    public long maxExecutionTimeInMillis() {
        return maxExecutionTime;
    }

    public void unregister() {
        Long previousValue = registry.remove(Thread.currentThread());
        if (previousValue == null) {
            throw new NullPointerException();
        }
        registered.decrementAndGet();
    }

    private void interruptLongRunningExecutions() {
        final long currentRelativeTime = relativeTimeSupplier.getAsLong();
        for (Map.Entry<Thread, Long> entry : registry.entrySet()) {
            if ((currentRelativeTime - entry.getValue()) > maxExecutionTime) {
                entry.getKey().interrupt();
                // not removing the entry here, this happens in the unregister() method.
            }
        }
        if (registered.get() > 0) {
            scheduler.apply(interval, this::interruptLongRunningExecutions);
        } else {
            running.set(false);
        }
    }
}
