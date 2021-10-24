package cn.sliew.milky.common.exception;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

/**
 * Rethrowing checked exceptions as unchecked ones. Eh, it is sometimes useful...
 */
public final class Rethrower {

    private Rethrower() {
        throw new AssertionError("No instance intended");
    }

    /**
     * Rethrow the supplied {@link Throwable exception} if it is
     * <em>unrecoverable</em>.
     *
     * <p>If the supplied {@code exception} is not <em>unrecoverable</em>, this
     * method does nothing.
     */
    public static void rethrowIfUnrecoverable(Throwable exception) {
        if (exception instanceof OutOfMemoryError) {
            throwAsUncheckedException(exception);
        }
    }

    /**
     * Throw the supplied {@link Throwable}, <em>masked</em> as an
     * unchecked exception.
     *
     * <p>The supplied {@code Throwable} will not be wrapped. Rather, it
     * will be thrown <em>as is</em> using an exploit of the Java language
     * that relies on a combination of generics and type erasure to trick
     * the Java compiler into believing that the thrown exception is an
     * unchecked exception even if it is a checked exception.
     *
     * <h3>Warning</h3>
     *
     * <p>This method should be used sparingly.
     *
     * @param t the {@code Throwable} to throw as an unchecked exception;
     *          never {@code null}
     * @return this method always throws an exception and therefore never
     * returns anything; the return type is merely present to allow this
     * method to be supplied as the operand in a {@code throw} statement
     */
    public static RuntimeException throwAsUncheckedException(Throwable t) {
        checkNotNull(t, () -> "Throwable must not be null");

        throwAs(t);
        // Appeasing the compiler: the following line will never be executed.
        return null;
    }

    /**
     * Rethrows <code>t</code> (identical object).
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void throwAs(Throwable t) throws T {
        throw (T) t;
    }
}

