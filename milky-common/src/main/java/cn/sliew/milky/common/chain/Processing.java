package cn.sliew.milky.common.chain;

/**
 * A <code>Processing</code> encapsulates states that can be returned by
 * commands. 
 * <p>
 * {@link Command}s should either return <code>FINISHED</code> if the
 * processing of the given context has been completed, or return
 * <code>CONTINUE</code> if the processing of the given {@link Context} should
 * be delegated to a subsequent command in an enclosing {@link Pipeline}.
 */
public enum Processing {

    /**
     * Commands should return continue if the processing of the given 
     * context should be delegated to a subsequent command in an enclosing chain.
     */
    CONTINUE,

    /**
     * Commands should return finished if the processing of the given context
     * has been completed.
     */
    FINISHED
    ;
}