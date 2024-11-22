package cn.sliew.milky.concurrent.thread;

import cn.sliew.milky.common.concurrent.DaemonThreadFactory;
import cn.sliew.milky.common.unit.TimeValue;
import cn.sliew.milky.common.unit.TimeValues;
import cn.sliew.milky.concurrent.thread.metrics.ThreadPoolExecutorMetrics;
import cn.sliew.milky.concurrent.thread.policy.AbortPolicyWithReport;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class ThreadPoolExecutorBuilder {

    public static ThreadPoolExecutorBuilder builder() {
        return new ThreadPoolExecutorBuilder();
    }

    private String name;
    private ThreadContext threadContext;

    private int coreSize = 1;
    private int maxSize = ExecutorUtil.availableProcessors();
    private TimeValue keepAlive = TimeValues.timeValueSeconds(60L);
    private BlockingQueue<Runnable> queue;
    private XRejectedExecutionHandler rejectedPolicy = new AbortPolicyWithReport("pool");

    private String threadNamePrefix;
    private int threadPriority = Thread.NORM_PRIORITY;
    private boolean daemon = false;

    private boolean waitForTasksToCompleteOnShutdown = false;
    private TimeValue awaitTermination = TimeValues.timeValueMillis(0L);

    private TaskDecorator taskDecorator;

    private MeterRegistry meterRegistry;

    private ThreadPoolExecutorBuilder() {

    }

    ThreadPoolExecutorBuilder name(String name) {
        this.name = name;
        return this;
    }

    ThreadPoolExecutorBuilder threadContext(ThreadContext threadContext) {
        this.threadContext = threadContext;
        return this;
    }

    ThreadPoolExecutorBuilder coreSize(int core) {
        this.coreSize = core;
        return this;
    }

    ThreadPoolExecutorBuilder maxSize(int max) {
        this.maxSize = max;
        return this;
    }

    ThreadPoolExecutorBuilder keepAliveTime(TimeValue aliveTime) {
        this.keepAlive = aliveTime;
        return this;
    }

    ThreadPoolExecutorBuilder blockingQueue(BlockingQueue<Runnable> queue) {
        this.queue = queue;
        return this;
    }

    ThreadPoolExecutorBuilder threadNamePrefix(String prefix) {
        this.threadNamePrefix = String.format("milky[%s]", prefix);
        return this;
    }

    ThreadPoolExecutorBuilder threadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
        return this;
    }

    ThreadPoolExecutorBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    ThreadPoolExecutorBuilder rejectedPolicy(XRejectedExecutionHandler rejectedPolicy) {
        this.rejectedPolicy = rejectedPolicy;
        return this;
    }

    ThreadPoolExecutorBuilder waitForTasksToCompleteOnShutdown(boolean wait) {
        this.waitForTasksToCompleteOnShutdown = wait;
        return this;
    }

    ThreadPoolExecutorBuilder awaitTerminationTime(TimeValue waitTime) {
        this.awaitTermination = waitTime;
        return this;
    }

    ThreadPoolExecutorBuilder taskDecorator(TaskDecorator decorator) {
        this.taskDecorator = taskDecorator;
        return this;
    }

    ThreadPoolExecutorBuilder meterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        return this;
    }

    MilkyThreadPoolExecutor build() {
        ThreadFactory threadFactory = buildThreadFacotry();
        MilkyThreadPoolExecutor executor = new MilkyThreadPoolExecutor(
                name, threadContext, coreSize, maxSize, keepAlive.millis(), TimeUnit.MILLISECONDS, queue,
                threadFactory, rejectedPolicy);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setAwaitTerminationMillis(awaitTermination.millis());
        if (this.meterRegistry != null) {
            new ThreadPoolExecutorMetrics(executor).bindTo(meterRegistry);
        }
        return executor;
    }

    private ThreadFactory buildThreadFacotry() {
        return new DaemonThreadFactory(threadNamePrefix, threadPriority, daemon);
    }
}
