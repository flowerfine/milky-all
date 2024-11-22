package cn.sliew.milky.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

public class DaemonThreadFactory implements ThreadFactory {

    private final String threadNamePrefix;
    private final int threadPriority;
    private final boolean daemon;

    private final ThreadGroup threadGroup;

    private final AtomicInteger threadCount = new AtomicInteger(0);

    public DaemonThreadFactory() {
        this(null);
    }

    public DaemonThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, true);
    }

    public DaemonThreadFactory(String threadNamePrefix, int threadPriority) {
        this(threadNamePrefix, threadPriority, true);
    }

    public DaemonThreadFactory(String threadNamePrefix, boolean daemon) {
        this(threadNamePrefix, Thread.NORM_PRIORITY, daemon);
    }

    public DaemonThreadFactory(String threadNamePrefix, int threadPriority, boolean daemon) {
        this(threadNamePrefix, threadPriority, daemon, (System.getSecurityManager() != null) ? System.getSecurityManager().getThreadGroup() : Thread.currentThread().getThreadGroup());
    }

    public DaemonThreadFactory(String threadNamePrefix, int threadPriority, boolean daemon, ThreadGroup threadGroup) {
        if (threadNamePrefix == null || threadNamePrefix.isEmpty()) {
            this.threadNamePrefix = getDefaultThreadNamePrefix();
        } else {
            this.threadNamePrefix = threadNamePrefix;
        }
        this.threadPriority = threadPriority;
        this.daemon = daemon;
        this.threadGroup = checkNotNull(threadGroup, () -> "threadGroup null");
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(threadGroup, runnable, nextThreadName());
        thread.setPriority(threadPriority);
        thread.setDaemon(daemon);
        return thread;
    }

    /**
     * Return the thread name to use for a newly created {@link Thread}.
     * <p>The default implementation returns the specified thread name prefix
     * with an increasing thread count appended: e.g. "[SimpleAsyncTaskExecutorT#0]".
     */
    protected String nextThreadName() {
        return String.format("[%sT#%d]", threadNamePrefix, this.threadCount.incrementAndGet());
    }

    /**
     * Build the default thread name prefix for this factory.
     *
     * @return the default thread name prefix (never {@code null})
     */
    protected String getDefaultThreadNamePrefix() {
        return getClass().getSimpleName();
    }

}
