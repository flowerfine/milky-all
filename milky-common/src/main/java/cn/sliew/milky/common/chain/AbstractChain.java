package cn.sliew.milky.common.chain;

public abstract class AbstractChain<K, V> implements Chain<K, V> {

    /**
     * <p>The list of {@link Command}s configured for this {@link Chain}, in
     * the order in which they may delegate processing to the remainder of
     * the {@link Chain}.</p>
     */
    protected Command<K, V>[] commands = new Command[0];

    /**
     * <p>Flag indicating whether the configuration of our commands list
     * has been frozen by a call to the <code>execute()</code> method.</p>
     */
    protected boolean frozen = false;

    /**
     * <p>Construct a {@link Chain} with no configured {@link Command}s.</p>
     */
    public AbstractChain() {

    }

    /**
     * <p>Construct a {@link Chain} configured with the specified
     * {@link Command}.</p>
     *
     * @param command The {@link Command} to be configured
     * @throws IllegalArgumentException if <code>command</code>
     *                                  is <code>null</code>
     */
    public AbstractChain(Command<K, V> command) {
        addCommand(command);
    }

    /**
     * <p>Construct a {@link Chain} configured with the specified
     * {@link Command}s.</p>
     *
     * @param commands The {@link Command}s to be configured
     * @throws IllegalArgumentException if <code>commands</code>,
     *                                  or one of the individual {@link Command} elements,
     *                                  is <code>null</code>
     */
    public AbstractChain(Command<K, V>[] commands) {
        if (commands == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < commands.length; i++) {
            addCommand(commands[i]);
        }
    }

    /**
     * See the {@link Chain} JavaDoc.
     *
     * @param command The {@link Command} to be added
     * @throws IllegalArgumentException if <code>command</code>
     *                                  is <code>null</code>
     * @throws IllegalStateException    if no further configuration is allowed
     */
    public void addCommand(Command<K, V> command) {
        if (command == null) {
            throw new IllegalArgumentException();
        }
        if (frozen) {
            throw new IllegalStateException();
        }
        Command[] results = new Command[commands.length + 1];
        System.arraycopy(commands, 0, results, 0, commands.length);
        results[commands.length] = command;
        commands = results;
    }

    /**
     * See the {@link Chain} JavaDoc.
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
    public boolean execute(Context<K, V> context) throws Exception {
        // Verify our parameters
        if (context == null) {
            throw new IllegalArgumentException();
        }

        // Freeze the configuration of the command list
        frozen = true;

        // Execute the commands in this list until one returns true
        // or throws an exception
        boolean saveResult = false;
        Exception saveException = null;
        int i = 0;
        int n = commands.length;
        for (i = 0; i < n; i++) {
            try {
                saveResult = commands[i].execute(context);
                if (saveResult) {
                    break;
                }
            } catch (Exception e) {
                saveException = e;
                break;
            }
        }

        // Call postprocess methods on Filters in reverse order
        if (i >= n) { // Fell off the end of the chain
            i--;
        }
        boolean handled = false;
        boolean result = false;
        for (int j = i; j >= 0; j--) {
            if (commands[j] instanceof Filter) {
                try {
                    result =
                            ((Filter) commands[j]).postprocess(context,
                                    saveException);
                    if (result) {
                        handled = true;
                    }
                } catch (Exception e) {
                    // Silently ignore
                }
            }
        }

        // Return the exception or result state from the last execute()
        if ((saveException != null) && !handled) {
            throw saveException;
        } else {
            return (saveResult);
        }
    }

    /**
     * <p>Return an array of the configured {@link Command}s for this
     * {@link Chain}.  This method is package private, and is used only
     * for the unit tests.</p>
     */
    Command<K, V>[] getCommands() {
        return (commands);
    }
}