package cn.sliew.milky.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.sliew.milky.common.check.Ensures.notBlank;

/**
 * todo thread context
 */
class DaemonThreadFactory implements ThreadFactory {

    private final int threadPriority = Thread.NORM_PRIORITY;

    private final String threadNamePrefix;

    private final boolean daemon;

    private final ThreadGroup threadGroup;

    private final AtomicInteger threadCount = new AtomicInteger(0);

    DaemonThreadFactory(String threadNamePrefix, boolean daemon, ThreadGroup threadGroup) {
        this.threadNamePrefix = notBlank(threadNamePrefix, () -> "threadNamePrefix empty");
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        this.threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
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
     * with an increasing thread count appended: e.g. "SimpleAsyncTaskExecutor-0".
     */
    protected String nextThreadName() {
        return threadNamePrefix + this.threadCount.incrementAndGet();
    }
}
