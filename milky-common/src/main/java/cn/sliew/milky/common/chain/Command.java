package cn.sliew.milky.common.chain;

import java.util.concurrent.CompletableFuture;

/**
 * 没有添加netty的@Shareable特性，如果Command被添加如多个pipeline，需要 {@link Command}
 * 处理并发等问题
 */
public interface Command<K, V> {

    /**
     * Execute a unit of processing work to be performed.
     * <p>
     * A command may either complete the required processing and return
     * finished, or delegate remaining processing to the subsequent command
     * in the enclosing {@link Pipeline} by returning continue.
     *
     * @param process The command context
     * @param context The {@link Context} to be processed by this {@link Command}
     * @param future  The result container
     * @throws PipelineException        general purpose exception return to indicate abnormal termination
     * @throws IllegalArgumentException if <code>context</code> is <code>null</code>
     */
    void onEvent(AbstractPipelineProcess<K, V> process, Context<K, V> context, CompletableFuture<?> future);

    /**
     * Gets called if a {@link Throwable} was thrown.
     */
    void exceptionCaught(AbstractPipelineProcess<K, V> process, Context<K, V> context, CompletableFuture<?> future, Throwable cause) throws PipelineException;
}
