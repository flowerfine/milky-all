package cn.sliew.milky.common.chain;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class DefaultPipelineTestCase extends MilkyTestCase {

    @Test
    public void test() {
        DefaultPipeline pipeline = new DefaultPipeline();
        pipeline.addFirst("hh", new Command() {
            @Override
            public void onEvent(AbstractPipelineProcess process, Context context, CompletableFuture future) {
            }

            @Override
            public void exceptionCaught(AbstractPipelineProcess process, Context context, CompletableFuture future, Throwable cause) throws PipelineException {

            }
        });
        pipeline.fireEvent(new ContextMap(), new CompletableFuture<>());
    }
}
