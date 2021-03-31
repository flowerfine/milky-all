package cn.sliew.milky.common.chain;

import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.fail;

public class DefaultPipelineTestCase extends MilkyTestCase {

    @Test
    public void testPipeline() {
        DefaultPipeline pipeline = new DefaultPipeline();
        pipeline.addFirst(LogCommand.NAME, new LogCommand(LoggerFactory.getLogger(DefaultPipelineTestCase.class)));
        pipeline.addAfter(LogCommand.NAME, ExceptionCommand.NAME, new ExceptionCommand());
        pipeline.addAfter(ExceptionCommand.NAME, LogCommand.NAME + "#0", new LogCommand(LoggerFactory.getLogger(DefaultPipelineTestCase.class)));
        ContextMap<String, Object> contextMap = new ContextMap<>();
        contextMap.put("foo", "bar");
        CompletableFuture<Object> future = new CompletableFuture<>();
        pipeline.fireEvent(contextMap, future);
        try {
            future.get();
            fail();
        } catch (Exception e) {

        }
    }

    public static class ExceptionCommand implements Command<String, Object> {

        private static final Logger logger = LoggerFactory.getLogger(ExceptionCommand.class);

        public static final String NAME = "ExceptionCommand";

        @Override
        public void onEvent(AbstractPipelineProcess<String, Object> process, Context<String, Object> context, CompletableFuture<?> future) {
            throw new RuntimeException("exception");
        }

        @Override
        public void exceptionCaught(AbstractPipelineProcess<String, Object> process, Context<String, Object> context, CompletableFuture<?> future, Throwable cause) throws PipelineException {
            logger.error(cause.getMessage(), cause);
            process.fireExceptionCaught(context, future, cause);
        }
    }
}
