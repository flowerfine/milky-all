package cn.sliew.milky.thread;

import java.util.concurrent.*;

class ThreadPoolExecutorBuilder {

    static ThreadPoolExecutorBuilder.Builder builder() {
        return new Builder();
    }

    static class Builder {

        private String name;
        private int coreSize;
        private int maxSize;
        private BlockingDeque<Runnable> workQueue;
        private long keepAliveTime;
        private TimeUnit unit;
        private boolean allowCoreThreadTimeOut = true;
        private RejectedExecutionHandler reject = new ThreadPoolExecutor.AbortPolicy();
        private ThreadFactory threadFactory;
        private boolean waitForTasksToCompleteOnShutdown = false;
        private int awaitTerminationSeconds = 0;
        private ThreadContext threadContext;
        private TaskDecorator taskDecorator;

        private Builder() {

        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder coreSize(int core) {
            this.coreSize = core;
            return this;
        }

        Builder maxSize(int max) {
            this.maxSize = max;
            return this;
        }

        Builder workQueue(BlockingDeque<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        Builder keepAliveTime(long keepAliveTime, TimeUnit unit) {
            this.keepAliveTime = keepAliveTime;
            this.unit = unit;
            return this;
        }

        Builder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
            return this;
        }

        Builder rejectedExecutionHandler(RejectedExecutionHandler reject) {
            this.reject = reject;
            return this;
        }

        Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        Builder waitForTasksToCompleteOnShutdown(boolean wait) {
            this.waitForTasksToCompleteOnShutdown = wait;
            return this;
        }

        Builder awaitTerminationSeconds(int waitTime) {
            this.awaitTerminationSeconds = waitTime;
            return this;
        }

        Builder threadContext(ThreadContext threadContext) {
            this.threadContext = threadContext;
            return this;
        }

        Builder taskDecorator(TaskDecorator decorator) {
            this.taskDecorator = decorator;
            return this;
        }

        MilkyThreadPoolExecutor build() {
            return new MilkyThreadPoolExecutor(name, threadContext, coreSize, maxSize, keepAliveTime, unit, workQueue, threadFactory);
        }

    }

    /**
     * A callback interface for a decorator to be applied to any {@link Runnable}
     * about to be executed.
     *
     * <p>The primary use case is to set some execution context around the task's
     * invocation, or to provide some monitoring/statistics for task execution.
     */
    @FunctionalInterface
    interface TaskDecorator {

        /**
         * Decorate the given {@code Runnable}, returning a potentially wrapped
         * {@code Runnable} for actual execution.
         *
         * @param runnable the original {@code Runnable}
         * @return the decorated {@code Runnable}
         */
        Runnable decorate(Runnable runnable);

    }
}
