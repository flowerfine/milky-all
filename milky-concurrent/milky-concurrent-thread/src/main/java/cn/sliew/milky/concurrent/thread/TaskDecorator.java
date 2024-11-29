package cn.sliew.milky.concurrent.thread;

/**
 * A callback interface for a decorator to be applied to any {@link Runnable}
 * about to be executed.
 *
 * <p>The primary use case is to set some execution context around the task's
 * invocation, or to provide some monitoring/statistics for task execution.
 */
@FunctionalInterface
public interface TaskDecorator {

    /**
     * Decorate the given {@code Runnable}.
     *
     * @param runnable the original {@code Runnable}
     * @return the decorated {@code Runnable}
     */
    Runnable decorate(Runnable runnable);

}