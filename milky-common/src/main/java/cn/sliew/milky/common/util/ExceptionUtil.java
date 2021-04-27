package cn.sliew.milky.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

public final class ExceptionUtil {

    private ExceptionUtil() {
        throw new IllegalStateException("can't do this!");
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

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void throwAs(Throwable t) throws T {
        throw (T) t;
    }

    /**
     * Read the stacktrace of the supplied {@link Throwable} into a String.
     */
    public static String readStackTrace(Throwable throwable) {
        checkNotNull(throwable, () -> "Throwable must not be null");
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
        }
        return stringWriter.toString();
    }
}
