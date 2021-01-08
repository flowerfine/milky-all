package cn.sliew.milky.common.chain;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface PipelineProcess<K, V, C extends Map<K, V>> {

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
    Command<K, V, C> command();

    /**
     * Return the assigned {@link Pipeline}
     */
    Pipeline<K, V, C> pipeline();

    PipelineProcess<K, V, C> fireEvent(Context<K, V> context, Future<?> future);

    /**
     * A {@link Command} received an {@link Throwable} in one of its operations.
     * <p>
     * This will result in having the  {@link Command#exceptionCaught(AbstractPipelineProcess, Context, Future, Throwable)}
     * method  called of the next  {@link Command} contained in the  {@link Pipeline}.
     */
    PipelineProcess fireExceptionCaught(Context<K, V> context, Future<?> future, Throwable cause);
}