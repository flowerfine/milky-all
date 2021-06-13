package cn.sliew.milky.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.notBlank;

/**
 * todo thread context。当创建线程的时候初始化线程的ThreadContext？？
 */
class DaemonThreadFactory implements ThreadFactory {

    private final int threadPriority = Thread.NORM_PRIORITY;

    private final String threadNamePrefix;
    private final boolean daemon;
    private final ThreadGroup threadGroup;

    private final AtomicInteger threadCount = new AtomicInteger(0);

    DaemonThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, true);
    }

    DaemonThreadFactory(String threadNamePrefix, boolean daemon) {
        this(threadNamePrefix, daemon, (System.getSecurityManager() != null) ? System.getSecurityManager().getThreadGroup() : Thread.currentThread().getThreadGroup());
    }

    DaemonThreadFactory(String threadNamePrefix, boolean daemon, ThreadGroup threadGroup) {
        if (threadNamePrefix == null || threadNamePrefix.isEmpty()) {
            this.threadNamePrefix = getDefaultThreadNamePrefix();
        } else {
            this.threadNamePrefix = threadNamePrefix;
        }
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
     * with an increasing thread count appended: e.g. "SimpleAsyncTaskExecutor[T#0]".
     */
    protected String nextThreadName() {
        return String.format("%s[T#%s]", threadNamePrefix, this.threadCount.incrementAndGet());
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
