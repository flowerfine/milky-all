package cn.sliew.milky.common.chain;

import cn.sliew.milky.common.log.Logger;

import java.util.concurrent.CompletableFuture;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

public class LogCommand implements Command<String, Object> {

    public static final String NAME = "LogCommand";

    private final Logger logger;

    public LogCommand(Logger logger) {
        this.logger = checkNotNull(logger);
    }

    @Override
    public void onEvent(AbstractPipelineProcess<String, Object> process, Context<String, Object> context, CompletableFuture<?> future) {
        logger.info("process: {}, context: {}", process, context);
        process.fireEvent(context, future);
    }

    @Override
    public void exceptionCaught(AbstractPipelineProcess<String, Object> process, Context<String, Object> context, CompletableFuture<?> future, Throwable cause) throws PipelineException {
        logger.error(cause.getMessage(), cause);
        process.fireExceptionCaught(context, future, cause);
    }
}
