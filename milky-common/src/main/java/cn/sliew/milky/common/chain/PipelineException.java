package cn.sliew.milky.common.chain;

import java.util.Map;

/**
 * Runtime Exception that wraps an underlying exception thrown during the
 * execution of a {@link Command} or {@link Pipeline}.
 */
public class PipelineException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 20120724L;

    /**
     * Context used when exception occurred.
     */
    private final Context<?, ?> context;

    /**
     * Command that failed when exception occurred.
     */
    private final Command<?, ?> failedCommand;

    /**
     * Create an exception object with a message.
     *
     * @param message Message to associate with exception
     */
    public PipelineException(String message) {
        super(message);
        this.context = null;
        this.failedCommand = null;
    }

    /**
     * Create an exception object with a message and chain it to another exception.
     *
     * @param message Message to associate with exception
     * @param cause   Exception to chain to this exception
     */
    public PipelineException(String message, Throwable cause) {
        super(message, cause);
        this.context = null;
        this.failedCommand = null;
    }

    /**
     * Constructs a new ChainException with references to the {@link Context}
     * and {@link Command} associated with the exception being wrapped (cause).
     *
     * @param <K>           Context key type
     * @param <V>           Context value type
     * @param message       the detail message. The detail message is saved for
     *                      later retrieval by the {@link #getMessage()} method.
     * @param cause         the cause (which is saved for later retrieval by the
     *                      {@link #getCause()} method).  (A <tt>null</tt> value is
     *                      permitted, and indicates that the cause is nonexistent or
     *                      unknown.)
     * @param context       The Context object passed to the {@link Command} in
     *                      which the exception occurred.
     * @param failedCommand The Command object in which the exception was
     *                      thrown.
     */
    public <K, V> PipelineException(String message, Throwable cause, Context<K, V> context, Command<K, V> failedCommand) {
        super(message, cause);
        this.context = context;
        this.failedCommand = failedCommand;
    }

    /**
     * @return The context object passed when the {@link Command}
     * threw an exception.
     */
    public Map<?, ?> getContext() {
        return context;
    }

    /**
     * @return The {@link Command} object in which the original exception was thrown.
     */
    public Command<?, ?> getFailedCommand() {
        return failedCommand;
    }

}