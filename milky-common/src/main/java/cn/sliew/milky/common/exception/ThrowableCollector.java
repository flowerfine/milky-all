package cn.sliew.milky.common.exception;

import cn.sliew.milky.common.util.ThrowableUtil;

import java.util.Optional;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

/**
 * Component that can collect one Throwable instance.
 * todo add lambda support, such as andThen or ifEmpty or whenTerminated and so on.
 */
public class ThrowableCollector {

    private volatile Optional<Throwable> throwableHolder;

    /**
     * Execute the supplied {@link Executable} and collect any {@link Throwable}
     * thrown during the execution.
     *
     * <p>If the {@code Executable} throws an <em>unrecoverable</em> exception
     * &mdash; for example, an {@link OutOfMemoryError} &mdash; this method will
     * rethrow it.
     *
     * @param executable the {@code Executable} to execute
     */
    public void execute(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable t) {
            ThrowableUtil.rethrowIfUnrecoverable(t);
            add(t);
        }
    }

    /**
     * Add the supplied {@link Throwable} to this {@code ThrowableCollector}.
     *
     * @param t the {@code Throwable} to add
     * @see #execute(Executable)
     */
    private void add(Throwable t) {
        checkNotNull(t, "Throwable must not be null");

        if (throwableHolder.isPresent()) {
            throwableHolder = Optional.of(t);
        } else {
            throwableHolder.get().addSuppressed(t);
        }
    }

    /**
     * Get the first {@link Throwable} collected by this {@code ThrowableCollector}.
     *
     * <p>If this collector is not empty, the first collected {@code Throwable}
     * will be returned with any additional {@code Throwables}
     * {@linkplain Throwable#addSuppressed(Throwable) suppressed} in the
     * first {@code Throwable}.
     *
     * @return the first collected {@code Throwable} or {@code null} if this
     * {@code ThrowableCollector} is empty
     * @see #isEmpty()
     */
    public Throwable getThrowable() {
        return throwableHolder.get();
    }

    /**
     * Determine if this {@code ThrowableCollector} is <em>empty</em> (i.e.,
     * has not collected any {@code Throwables}).
     */
    public boolean isEmpty() {
        return !throwableHolder.isPresent();
    }

    /**
     * Determine if this {@code ThrowableCollector} is <em>not empty</em> (i.e.,
     * has collected at least one {@code Throwable}).
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * Functional interface for an executable block of code that may throw a {@link Throwable}.
     */
    @FunctionalInterface
    public interface Executable {

        /**
         * Execute this executable, potentially throwing a {@link Throwable}
         * that signals abortion or failure.
         */
        void execute() throws Throwable;

    }

    /**
     * Factory for {@code ThrowableCollector} instances.
     */
    public interface Factory {

        /**
         * Create a new instance of a {@code ThrowableCollector}.
         */
        ThrowableCollector create();

    }
}
