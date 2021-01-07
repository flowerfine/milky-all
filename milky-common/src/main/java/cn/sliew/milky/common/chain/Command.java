package cn.sliew.milky.common.chain;

import java.util.Map;
import java.util.concurrent.Future;

public interface Command<K, V, C extends Map<K, V>> {

    /**
     * Execute a unit of processing work to be performed.
     * <p>
     * A command may either complete the required processing and return
     * finished, or delegate remaining processing to the subsequent command
     * in the enclosing {@link Pipeline} by returning continue.
     *
     * @param context The {@link Context} to be processed by this
     *                {@link Command}
     * @param future  The result container
     * @return {@link Processing#FINISHED} if the processing of this contex
     * has been completed. Returns {@link Processing#CONTINUE} if the processing
     * of this context should be delegated to a subsequent command in an
     * enclosing chain.
     * @throws PipelineException        general purpose exception return to indicate abnormal termination
     * @throws IllegalArgumentException if <code>context</code> is <code>null</code>
     */
    Processing execute(C context, Future<?> future);

    /**
     * Gets called if a {@link Throwable} was thrown.
     */
    void exceptionCaught(Command<K, V, C> command, Throwable cause) throws PipelineException;
}
