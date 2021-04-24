package cn.sliew.milky.common.exception;

import cn.sliew.milky.common.util.ThrowableUtil;

import java.util.Optional;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

/**
 * Component that can collect one Throwable instance.
 */
public class ThrowableCollector<T> {

    /**
     * {@link Throwable} holder.
     */
    private volatile Optional<Throwable> throwableHolder = Optional.empty();

    /**
     * prevent construct directly, suggest to static factory method {@link ThrowableCollector#create()}.
     */
    private ThrowableCollector() {

    }

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
        execute(() -> {
            executable.execute();
            return null;
        });
    }

    public Optional<T> execute(ExecutableWithResult<T> executable) {
        try {
            return Optional.ofNullable(executable.execute());
        } catch (Throwable t) {
            ThrowableUtil.rethrowIfUnrecoverable(t);
            add(t);
            return Optional.empty();
        }
    }

    /**
     * Add the supplied {@link Throwable} to this {@code ThrowableCollector}.
     *
     * @param t the {@code Throwable} to add
     * @see #execute(Executable)
     */
    private void add(Throwable t) {
        checkNotNull(t, () -> "Throwable must not be null");

        if (throwableHolder.isPresent()) {
            throwableHolder.get().addSuppressed(t);
        } else {
            throwableHolder = Optional.of(t);
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
     */
    public Optional<Throwable> getThrowable() {
        return throwableHolder;
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
     * Functional interface for an executable block of code that may throw a {@link Throwable}.
     *
     * @param <T> result type
     */
    @FunctionalInterface
    public interface ExecutableWithResult<T> {

        /**
         * Execute this executable, potentially throwing a {@link Throwable}
         * that signals abortion or failure.
         *
         * @return executable result
         * @throws Throwable
         */
        T execute() throws Throwable;

    }

    /**
     * Factory method for {@code ThrowableCollector} instances creation.
     *
     * @return throwable collector
     */
    public static <T> ThrowableCollector<T> create() {
        return new ThrowableCollector();
    }

}
