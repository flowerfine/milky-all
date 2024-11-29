package cn.sliew.milky.concurrent.thread.policy;

import cn.sliew.milky.common.concurrent.RunnableWrapper;
import cn.sliew.milky.concurrent.thread.SizeBlockingQueue;
import cn.sliew.milky.concurrent.thread.XRejectedExecutionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * fixme: 提供关闭 dump 线程池开关，dump 线程池是一个 debug 功能，在生产环境应该关闭 debug 类功能。
 * 当发生线程池打满的情况时，另起一个线程 dump 线程池以便排查线上问题。
 * 支持强制执行策略。如果使用的不是固定大小的工作队列，当线程池队列满了的时候会一直等待入队。没有采用 {@code CallerRunsPolicy}
 * 策略的原因是线程池会阻塞主线程执行任务，不太合适。
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy implements XRejectedExecutionHandler {

    private static final Logger log = LogManager.getLogger(AbortPolicyWithReport.class);

    private final AtomicInteger rejected = new AtomicInteger();

    private final String name;

    private static volatile long lastPrintTime = 0;

    /**
     * todo 时间单位
     */
    private static final long TEN_MINUTES_MILLS = 10 * 60 * 1000;

    private static final String OS_WIN_PREFIX = "win";

    private static final String OS_NAME_KEY = "os.name";

    private static final String WIN_DATETIME_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";

    private static Semaphore guard = new Semaphore(1);

    public AbortPolicyWithReport(String name) {
        this.name = name;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("Thread pool is EXHAUSTED for %dth times!" +
                        " Thread Pool Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: "
                        + "%d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)",
                rejected.incrementAndGet(), name, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(),
                e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
        log.warn(msg);
        dumpJStack();

        if (r instanceof RunnableWrapper) {
            if (((RunnableWrapper) r).isForceExecution()) {
                BlockingQueue<Runnable> queue = e.getQueue();
                if (!(queue instanceof SizeBlockingQueue)) {
                    throw new IllegalStateException("forced execution, but expected a size queue");
                }
                try {
                    ((SizeBlockingQueue) queue).forcePut(r);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("forced execution, but got interrupted", ex);
                }
                return;
            }
        }

        throw new RejectedExecutionException(msg);
    }

    private void dumpJStack() {
        long now = System.currentTimeMillis();

        //dump every 10 minutes
        if (now - lastPrintTime < TEN_MINUTES_MILLS) {
            return;
        }

        if (!guard.tryAcquire()) {
            return;
        }

        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            String dumpPath = System.getProperty("user.home");

            DateTimeFormatter formatter;

            String os = System.getProperty(OS_NAME_KEY).toLowerCase();


            // window system don't support ":" in file name
            if (os.contains(OS_WIN_PREFIX)) {
                formatter = DateTimeFormatter.ofPattern(WIN_DATETIME_FORMAT);
            } else {
                formatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
            }

            String dateStr = formatter.format(LocalDateTime.now());
            //try-with-resources
            try (FileOutputStream jStackStream = new FileOutputStream(
                    new File(dumpPath, "Milky_JStack.log" + "." + dateStr))) {
                JVMUtil.jstack(jStackStream);
            } catch (Throwable t) {
                log.error("dump jStack error", t);
            } finally {
                guard.release();
            }
            lastPrintTime = System.currentTimeMillis();
        });
        //must shutdown thread pool ,if not will lead to OOM
        pool.shutdown();

    }

    @Override
    public long rejected() {
        return rejected.get();
    }
}
