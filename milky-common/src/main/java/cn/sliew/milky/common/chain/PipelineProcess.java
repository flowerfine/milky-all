package cn.sliew.milky.common.chain;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface PipelineProcess<K, V> {

    /**
     * The unique name of the {@link Command}.The name was used when then {@link Command}
     * was added to the {@link Pipeline}. This name can also be used to access the registered
     * {@link Command} from the {@link Pipeline}.
     */
    String name();

    /**
     * Returns the {@link Executor} which is used to execute an arbitrary task.
     */
    Executor executor();

    /**
     * The {@link Command} that is bound this {@link PipelineProcess}.
     */
    Command<K, V> command();

    /**
     * Return the assigned {@link Pipeline}
     */
    Pipeline<K, V> pipeline();

    PipelineProcess<K, V> fireEvent(Context<K, V> context, CompletableFuture<?> future);

    /**
     * A {@link Command} received an {@link Throwable} in one of its operations.
     * <p>
     * This will result in having the
     * {@link Command#exceptionCaught(AbstractPipelineProcess, Context, java.util.concurrent.CompletableFuture, Throwable)}
     * method called of the next {@link Command} contained in the {@link Pipeline}.
     */
    PipelineProcess fireExceptionCaught(Context<K, V> context, CompletableFuture<?> future, Throwable cause);
}