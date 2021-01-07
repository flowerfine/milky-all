package cn.sliew.milky.common.chain;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;

import java.util.concurrent.Executor;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

abstract class AbstractPipelineProcess implements PipelineProcess {

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
        return null;
    }

    @Override
    public Executor executor() {
        return null;
    }

    @Override
    public Command command() {
        return null;
    }

    @Override
    public Pipeline pipeline() {
        return null;
    }

    @Override
    public Pipeline fireEvent() {
        return null;
    }

    @Override
    public Pipeline fireExceptionCaught(Throwable cause) {
        return null;
    }
}
