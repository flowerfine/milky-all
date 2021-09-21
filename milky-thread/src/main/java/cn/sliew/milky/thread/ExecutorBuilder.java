package cn.sliew.milky.thread;

import cn.sliew.milky.common.unit.TimeValue;
import cn.sliew.milky.common.unit.TimeValues;

import java.util.concurrent.*;

class ExecutorBuilder {

    private String name;
    private ThreadContext threadContext;

    private int coreSize = 1;
    private int maxSize = ExecutorUtil.availableProcessors();
    private TimeValue keepAlive = TimeValues.timeValueSeconds(60L);
    private BlockingQueue<Runnable> queue;
    private RejectedExecutionHandler rejectedPolicy = new ThreadPoolExecutor.AbortPolicy();

    private String threadNamePrefix;
    private int threadPriority = Thread.NORM_PRIORITY;
    private boolean daemon = false;

    private boolean waitForTasksToCompleteOnShutdown = false;
    private TimeValue awaitTermination = TimeValues.timeValueMillis(0L);

    private TaskDecorator taskDecorator;

    ExecutorBuilder() {

    }

    private ExecutorBuilder name(String name) {
        this.name = name;
        return this;
    }

    private ExecutorBuilder threadContext(ThreadContext threadContext) {
        this.threadContext = threadContext;
        return this;
    }

    private ExecutorBuilder coreSize(int core) {
        this.coreSize = core;
        return this;
    }

    private ExecutorBuilder maxSize(int max) {
        this.maxSize = max;
        return this;
    }

    private ExecutorBuilder keepAliveTime(TimeValue aliveTime) {
        this.keepAlive = aliveTime;
        return this;
    }

    private ExecutorBuilder blockingQueue(BlockingQueue<Runnable> queue) {
        this.queue = queue;
        return this;
    }

    private ExecutorBuilder threadNamePrefix(String prefix) {
        this.threadNamePrefix = String.format("milky[%s]", prefix);
        return this;
    }

    private ExecutorBuilder threadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
        return this;
    }

    private ExecutorBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    private ExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler reject) {
        this.taskExecutor.setRejectedExecutionHandler(reject);
        return this;
    }

    private ExecutorBuilder waitForTasksToCompleteOnShutdown(boolean wait) {
        this.waitForTasksToCompleteOnShutdown = wait;
        return this;
    }

    private ExecutorBuilder awaitTerminationTime(TimeValue waitTime) {
        this.awaitTermination = waitTime;
        return this;
    }

    private ExecutorBuilder taskDecorator(TaskDecorator decorator) {
        this.taskDecorator = taskDecorator;
        return this;
    }

    private MilkyThreadPoolExecutor build() {
        ThreadFactory threadFactory = buildThreadFacotry();
        MilkyThreadPoolExecutor executor = new MilkyThreadPoolExecutor(
                name, threadContext, coreSize, maxSize, keepAlive.getMillis(), TimeUnit.MILLISECONDS, queue,
                threadFactory, rejectedPolicy);
        return executor;
    }

    private ThreadFactory buildThreadFacotry() {
        return new DaemonThreadFactory(threadNamePrefix, threadPriority, daemon);
    }
}
