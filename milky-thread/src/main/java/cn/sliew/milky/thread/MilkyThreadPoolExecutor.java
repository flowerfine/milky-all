package cn.sliew.milky.thread;

import java.util.concurrent.*;

public class MilkyThreadPoolExecutor extends ThreadPoolExecutor {

    private final ThreadContext threadContext;
    private volatile ShutdownListener listener;

    private final Object monitor = new Object();
    /**
     * Name used in error reporting.
     */
    private final String name;

    final String getName() {
        return name;
    }


    public MilkyThreadPoolExecutor(String name, ThreadContext threadContext, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(name, threadContext, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new DaemonThreadFactory(name + "-pool-", true, Thread.currentThread().getThreadGroup()), new AbortPolicyWithReport(name));
    }

    public MilkyThreadPoolExecutor(String name, ThreadContext threadContext, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(name, threadContext, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new AbortPolicyWithReport(name));
    }

    public MilkyThreadPoolExecutor(String name, ThreadContext threadContext, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, XRejectedExecutionHandler handler) {
        this(name, threadContext, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new DaemonThreadFactory(name + "-pool-", true, Thread.currentThread().getThreadGroup()), handler);
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
            super.execute(command);
        } catch (RejectedExecutionException ex) {
            if (command instanceof RunnableWrapper) {
                // If we are an custom runnable wrapper we can handle the rejection
                // directly and don't need to rethrow it.
                try {
                    ((RunnableWrapper) command).onRejection(ex);
                } finally {
                    ((RunnableWrapper) command).onAfter();
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


    public interface ShutdownListener {
        void onTerminated();
    }
}
