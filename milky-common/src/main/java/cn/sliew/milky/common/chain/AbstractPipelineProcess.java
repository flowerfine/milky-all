package cn.sliew.milky.common.chain;

import cn.sliew.milky.common.exception.ThrowableUtil;
import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

abstract class AbstractPipelineProcess<K, V> implements PipelineProcess<K, V, Context<K, V>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPipelineProcess.class);

    volatile AbstractPipelineProcess next;
    volatile AbstractPipelineProcess prev;


    private final DefaultPipeline pipeline;
    private final String name;

    final Executor executor;

    AbstractPipelineProcess(DefaultPipeline pipeline, Executor executor, String name) {
        this.name = checkNotNull(name, "name");
        this.pipeline = pipeline;
        this.executor = executor;
    }


    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Executor executor() {
        return this.executor;
    }

    @Override
    public Pipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public PipelineProcess<K, V, Context<K, V>> fireEvent(Context<K, V> context, Future<?> future) {
        invokeEvent(this.next, context, future);
        return this;
    }

    @Override
    public PipelineProcess fireExceptionCaught(Context<K, V> context, Future<?> future, Throwable cause) {
        invokeExceptionCaught(this.next, context, future, cause);
        return this;
    }

    /**
     * fixme 判断是否为同一个线程内运行
     */
    static <K, V> void invokeEvent(final AbstractPipelineProcess next, final Context<K, V> context, final Future<?> future) {
        next.invokeEvent(context, future);
    }

    private void invokeEvent(final Context<K, V> context, final Future<?> future) {
        try {
            command().onEvent(this, context, future);
        } catch (Throwable t) {
            invokeExceptionCaught(context, future, t);
        }
    }

    static <K, V> void invokeExceptionCaught(final AbstractPipelineProcess next, final Context<K, V> context, final Future<?> future, final Throwable cause) {
        try {
            next.invokeExceptionCaught(context, future, cause);
        } catch (Throwable t) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to submit an exceptionCaught() event.", t);
                logger.warn("The exceptionCaught() event that was failed to submit was:", cause);
            }
        }
    }

    private void invokeExceptionCaught(final Context<K, V> context, final Future<?> future, final Throwable cause) {
        try {
            command().exceptionCaught(this, context, future, cause);
        } catch (Throwable error) {
            if (logger.isDebugEnabled()) {
                logger.debug("An exception {} was thrown by a user handler's exceptionCaught() " +
                                "method while handling the following exception:",
                        ThrowableUtil.stackTraceToString(error), cause);
            } else if (logger.isWarnEnabled()) {
                logger.warn("An exception '{}' [enable DEBUG level for full stacktrace] " +
                        "was thrown by a user handler's exceptionCaught() " +
                        "method while handling the following exception:", error, cause);
            }
        }
    }
}