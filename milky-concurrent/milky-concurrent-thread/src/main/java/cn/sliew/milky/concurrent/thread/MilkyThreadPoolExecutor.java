package cn.sliew.milky.concurrent.thread;

import cn.sliew.milky.common.collect.ConcurrentReferenceHashMap;
import cn.sliew.milky.common.concurrent.RunnableWrapper;
import cn.sliew.milky.common.exception.Rethrower;
import cn.sliew.milky.concurrent.thread.policy.AbortPolicyWithReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.*;

public class MilkyThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger log = LogManager.getLogger(MilkyThreadPoolExecutor.class);

    /**
     * Name used in error reporting.
     */
    private final String name;
    private final ThreadContext threadContext;
    private volatile ShutdownListener listener;

    private final Object monitor = new Object();

    private RejectedExecutionHandler rejectedExecutionHandler = new AbortPolicy();

    private ThreadFactory threadFactory;

    private boolean waitForTasksToCompleteOnShutdown = false;
    private long awaitTerminationMillis = 0L;

    private TaskDecorator taskDecorator;

    // Runnable decorator to user-level FutureTask, if different
    private final Map<Runnable, Object> decoratedTaskMap =
            new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public final String getName() {
        return name;
    }

    /**
     * Set whether to wait for scheduled tasks to complete on shutdown,
     * not interrupting running tasks and executing all tasks in the queue.
     * <p>Default is "false", shutting down immediately through interrupting
     * ongoing tasks and clearing the queue. Switch this flag to "true" if you
     * prefer fully completed tasks at the expense of a longer shutdown phase.
     * <p>Note that Spring's container shutdown continues while ongoing tasks
     * are being completed. If you want this executor to block and wait for the
     * termination of tasks before the rest of the container continues to shut
     * down - e.g. in order to keep up other resources that your tasks may need -,
     * set the {@link #setAwaitTerminationSeconds "awaitTerminationSeconds"}
     * property instead of or in addition to this property.
     */
    public void setWaitForTasksToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }

    public void setAwaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
    }

    public MilkyThreadPoolExecutor(String name, ThreadContext threadContext, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(name, threadContext, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new DaemonThreadFactory(name + "-pool"), new AbortPolicyWithReport(name));
    }

    public MilkyThreadPoolExecutor(String name, ThreadContext threadContext, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(name, threadContext, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new AbortPolicyWithReport(name));
    }

    public MilkyThreadPoolExecutor(String name, ThreadContext threadContext, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, XRejectedExecutionHandler handler) {
        this(name, threadContext, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new DaemonThreadFactory(name + "-pool"), handler);
    }

    public MilkyThreadPoolExecutor(String name, ThreadContext threadContext, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, XRejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.name = name;
        this.threadContext = threadContext;
    }

    /**
     * todo thread context
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    public void execute(Runnable command) {
        try {
            if (taskDecorator != null) {
                Runnable decorated = taskDecorator.decorate(command);
                if (decorated != command) {
                    decoratedTaskMap.put(decorated, command);
                }
                super.execute(decorated);
            } else {
                super.execute(command);
            }
        } catch (RejectedExecutionException ex) {
            if (command instanceof RunnableWrapper) {
                // If we are an custom runnable wrapper we can handle the rejection
                // directly and don't need to rethrow it.
                try {
                    ((RunnableWrapper) command).onRejection(ex);
                } finally {
                    try {
                        ((RunnableWrapper) command).onAfter();
                    } catch (Exception e) {
                        Rethrower.throwAs(e);
                    }
                }
            } else {
                throw ex;
            }
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    protected synchronized void terminated() {
        super.terminated();
        synchronized (monitor) {
            if (listener != null) {
                try {
                    listener.onTerminated();
                } finally {
                    listener = null;
                }
            }
        }
    }

    @Override
    public final String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append('[');
        b.append("name = ").append(name).append(", ");
//        if (getQueue() instanceof SizeBlockingQueue) {
//            @SuppressWarnings("rawtypes")
//            SizeBlockingQueue queue = (SizeBlockingQueue) getQueue();
//            b.append("queue capacity = ").append(queue.capacity()).append(", ");
//        }
        appendThreadPoolExecutorDetails(b);
        /*
         * ThreadPoolExecutor has some nice information in its toString but we
         * can't get at it easily without just getting the toString.
         */
        b.append(super.toString()).append(']');
        return b.toString();
    }

    /**
     * Append details about this thread pool to the specified {@link StringBuilder}. All details should be appended as key/value pairs in
     * the form "%s = %s, "
     *
     * @param sb the {@link StringBuilder} to append to
     */
    protected void appendThreadPoolExecutorDetails(final StringBuilder sb) {

    }

    @Override
    public void shutdown() {
        if (log.isInfoEnabled()) {
            log.info(String.format("Shutting down ExecutorService %s", this.name));
        }
        if (this.waitForTasksToCompleteOnShutdown) {
            super.shutdown();
        } else {
            for (Runnable remainingTask : super.shutdownNow()) {
                cancelRemainingTask(remainingTask);
            }
        }
        awaitTerminationIfNecessary();
    }

    /**
     * Cancel the given remaining task which never commended execution,
     * as returned from {@link ExecutorService#shutdownNow()}.
     *
     * @param task the task to cancel (typically a {@link RunnableFuture})
     */
    protected void cancelRemainingTask(Runnable task) {
        if (task instanceof Future) {
            ((Future<?>) task).cancel(true);
        }
        // Cancel associated user-level Future handle as well
        Object original = this.decoratedTaskMap.get(task);
        if (original instanceof Future) {
            ((Future<?>) original).cancel(true);
        }
    }

    /**
     * Wait for the executor to terminate, according to the value of the
     * {@code awaitTerminationSeconds} property.
     */
    private void awaitTerminationIfNecessary() {
        if (this.awaitTerminationMillis > 0) {
            try {
                if (!awaitTermination(this.awaitTerminationMillis, TimeUnit.MILLISECONDS)) {
                    if (log.isWarnEnabled()) {
                        log.warn(String.format("Timed out while waiting for executor %s to terminate", this.name));
                    }
                }
            } catch (InterruptedException ex) {
                if (log.isWarnEnabled()) {
                    log.warn(String.format("Interrupted while waiting for executor %s to terminate", this.name));
                }
                Thread.currentThread().interrupt();
            }
        }
    }

    public interface ShutdownListener {
        void onTerminated();
    }
}
