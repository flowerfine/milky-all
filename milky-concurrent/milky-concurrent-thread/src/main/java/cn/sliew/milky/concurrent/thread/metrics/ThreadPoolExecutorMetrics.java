package cn.sliew.milky.concurrent.thread.metrics;

import cn.sliew.milky.concurrent.thread.MilkyThreadPoolExecutor;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

public class ThreadPoolExecutorMetrics implements MeterBinder {

    private final MilkyThreadPoolExecutor executor;
    private final Iterable<Tag> tags;

    public ThreadPoolExecutorMetrics(MilkyThreadPoolExecutor executor, Tag... tags) {
        this.executor = checkNotNull(executor);
        this.tags = Arrays.asList(tags);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        String name = this.executor.getName();
        FunctionCounter.builder("executor.completed", this.executor, ThreadPoolExecutor::getCompletedTaskCount)
                .tags(this.tags).tag("name", name)
                .description("The approximate total number of tasks that have completed execution")
                .baseUnit("tasks")
                .register(registry);
        Gauge.builder("executor.active", this.executor, ThreadPoolExecutor::getActiveCount)
                .tags(this.tags).tag("name", name)
                .description("The approximate number of threads that are actively executing tasks")
                .baseUnit("threads")
                .register(registry);
        Gauge.builder("executor.queued", this.executor, (tpRef) -> (double) tpRef.getQueue().size())
                .tags(this.tags).tag("name", name)
                .description("The approximate number of tasks that are queued for execution")
                .baseUnit("tasks")
                .register(registry);
        Gauge.builder("executor.queue.remaining", this.executor, (tpRef) -> (double) tpRef.getQueue().remainingCapacity())
                .tags(this.tags).tag("name", name)
                .description("The number of additional elements that this queue can ideally accept without blocking")
                .baseUnit("tasks")
                .register(registry);
        Gauge.builder("executor.pool.size", this.executor, ThreadPoolExecutor::getPoolSize)
                .tags(this.tags).tag("name", name)
                .description("The current number of threads in the pool")
                .baseUnit("threads")
                .register(registry);
        Gauge.builder("executor.pool.core", this.executor, ThreadPoolExecutor::getCorePoolSize).
                tags(this.tags).tag("name", name)
                .description("The core number of threads for the pool")
                .baseUnit("threads")
                .register(registry);
        Gauge.builder("executor.pool.max", this.executor, ThreadPoolExecutor::getMaximumPoolSize)
                .tags(this.tags).tag("name", name)
                .description("The maximum allowed number of threads in the pool")
                .baseUnit("threads")
                .register(registry);
    }
}
