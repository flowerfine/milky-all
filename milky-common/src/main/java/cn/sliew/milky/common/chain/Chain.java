package cn.sliew.milky.common.chain;

public interface Chain<K, V> extends Command<K, V> {

    /**
     * <p>Add a {@link Command} to the list of {@link Command}s that will
     * be called in turn when this {@link Chain}'s <code>execute()</code>
     * method is called.  Once <code>execute()</code> has been called
     * at least once, it is no longer possible to add additional
     * {@link Command}s; instead, an exception will be thrown.</p>
     *
     * @param command The {@link Command} to be added
     * @throws IllegalArgumentException if <code>command</code>
     *                                  is <code>null</code>
     * @throws IllegalStateException    if this {@link Chain} has already
     *                                  been executed at least once, so no further configuration is allowed
     */
    void addCommand(Command<K, V> command);


    /**
     * <p>Execute the processing represented by this {@link Chain} according
     * to the following algorithm.</p>
     * <ul>
     * <li>If there are no configured {@link Command}s in the {@link Chain},
     *     return <code>false</code>.</li>
     * <li>Call the <code>execute()</code> method of each {@link Command}
     *     configured on this chain, in the order they were added via calls
     *     to the <code>addCommand()</code> method, until the end of the
     *     configured {@link Command}s is encountered, or until one of
     *     the executed {@link Command}s returns <code>true</code>
     *     or throws an exception.</li>
     * <li>Walk backwards through the {@link Command}s whose
     *     <code>execute()</code> methods, starting with the last one that
     *     was executed.  If this {@link Command} instance is also a
     *     {@link Filter}, call its <code>postprocess()</code> method,
     *     discarding any exception that is thrown.</li>
     * <li>If the last {@link Command} whose <code>execute()</code> method
     *     was called threw an exception, rethrow that exception.</li>
     * <li>Otherwise, return the value returned by the <code>execute()</code>
     *     method of the last {@link Command} that was executed.  This will be
     *     <code>true</code> if the last {@link Command} indicated that
     *     processing of this {@link Context} has been completed, or
     *     <code>false</code> if none of the called {@link Command}s
     *     returned <code>true</code>.</li>
     * </ul>
     *
     * @param context The {@link Context} to be processed by this
     *                {@link Chain}
     * @return <code>true</code> if the processing of this {@link Context}
     * has been completed, or <code>false</code> if the processing
     * of this {@link Context} should be delegated to a subsequent
     * {@link Command} in an enclosing {@link Chain}
     * @throws Exception                if thrown by one of the {@link Command}s
     *                                  in this {@link Chain} but not handled by a <code>postprocess()</code>
     *                                  method of a {@link Filter}
     * @throws IllegalArgumentException if <code>context</code>
     *                                  is <code>null</code>
     */
    boolean execute(Context<K, V> context) throws Exception;

}