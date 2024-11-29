package cn.sliew.milky.common.concurrent;

import java.util.concurrent.*;

public enum ThreadPoolUtil {
    ;

    private static ConcurrentMap<String, ExecutorService> executors = new ConcurrentHashMap<>();

    private static ExecutorService init(String poolName, int poolSize) {
        return new ThreadPoolExecutor(poolSize, poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DaemonThreadFactory("Pool-" + poolName, true),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ExecutorService getOrInitExecutors(String poolName, int poolSize) {
        return executors.computeIfAbsent(poolName, key -> init(poolName, poolSize));
    }

    public static void releaseExecutors(String poolName) {
        ExecutorService executorService = executors.remove(poolName);
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
