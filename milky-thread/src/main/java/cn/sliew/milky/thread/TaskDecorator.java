package cn.sliew.milky.thread;

/**
 * A callback interface for a decorator to be applied to any {@link Runnable}
 * about to be executed.
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