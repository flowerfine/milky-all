package cn.sliew.milky.common.chain;

public interface Command<K, V> {

    /**
     * Commands should return <code>CONTINUE_PROCESSING</code> if the processing
     * of the given {@link Context} should be delegated to a subsequent
     * {@link Command} in an enclosing {@link Chain}.
     */
    public static final boolean CONTINUE_PROCESSING = false;

    /**
     * Commands should return <code>PROCESSING_COMPLETE</code>
     * if the processing of the given {@link Context}
     * has been completed.
     */
    public static final boolean PROCESSING_COMPLETE = true;

    /**
     * <p>Execute a unit of processing work to be performed.  This
     * {@link Command} may either complete the required processing
     * and return <code>true</code>, or delegate remaining processing
     * to the next {@link Command} in a {@link Chain} containing this
     * {@link Command} by returning <code>false</code>
     *
     * @param context The {@link Context} to be processed by this
     *                {@link Command}
     * @return <code>true</code> if the processing of this {@link Context}
     * has been completed, or <code>false</code> if the processing
     * of this {@link Context} should be delegated to a subsequent
     * {@link Command} in an enclosing {@link Chain}
     * @throws Exception                general purpose exception return
     *                                  to indicate abnormal termination
     * @throws IllegalArgumentException if <code>context</code>
     *                                  is <code>null</code>
     */
    boolean execute(Context<K, V> context) throws Exception;

}
