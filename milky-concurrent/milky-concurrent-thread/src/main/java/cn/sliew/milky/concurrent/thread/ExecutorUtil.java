package cn.sliew.milky.concurrent.thread;

import java.util.concurrent.*;

import static cn.sliew.milky.common.check.Ensures.checkArgument;

class ExecutorUtil {

    private static int processors = Runtime.getRuntime().availableProcessors();

    private ExecutorUtil() {
        throw new UnsupportedOperationException("no instance");
    }

    /**
     * Constrains a value between minimum and maximum values
     * (inclusive).
     *
     * @param value the value to constrain
     * @param min   the minimum acceptable value
     * @param max   the maximum acceptable value
     * @return min if value is less than min, max if value is greater
     * than value, otherwise value
     */
    static int bounded(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    static int boundedByUpper(int value, int max) {
        return bounded(value, 1, max);
    }

    static int boundedBylower(int value, int min) {
        return bounded(value, min, Integer.MAX_VALUE);
    }

    static int halfProcessors() {
        return (processors + 1) / 2;
    }

    static int availableProcessors() {
        return processors;
    }

    static int oneAndhalfProcessors() {
        return ((processors * 3) / 2) + 1;
    }

    static int twiceProcessors() {
        return processors * 2;
    }

    /**
     * Create the BlockingQueue to use for the ThreadPoolExecutor.
     * <p>A LinkedBlockingQueue instance will be created for a positive
     * capacity value; a SynchronousQueue else.
     * @param queueCapacity the specified queue capacity
     * @return the BlockingQueue instance
     * @see LinkedBlockingQueue
     * @see SynchronousQueue
     */
    static BlockingQueue<Runnable> boundedQueue(int queueCapacity) {
        checkArgument(queueCapacity > 0, () -> "queueCapacity must greater than 0");
        return new LinkedBlockingQueue<>(queueCapacity);
    }

    static BlockingQueue<Runnable> unboundedQueue() {
        return new LinkedTransferQueue<>();
    }

    static BlockingQueue<Runnable> zeroQueue() {
        return new SynchronousQueue<>();
    }

    static BlockingQueue<Runnable> eagerQueue(ThreadPoolExecutor executor) {
        return new ThreadEagerQueue<>(executor);
    }

    static BlockingQueue<Runnable> resizableQueue(BlockingQueue<Runnable> queue, int initialCapacity) {
        return new ResizableBlockingQueue<>(queue, initialCapacity);
    }
}
