package cn.sliew.milky.common.chain;

import java.util.concurrent.Executor;

final class DefaultPipelineProcess extends AbstractPipelineProcess {

    private final Command command;

    DefaultPipelineProcess(DefaultPipeline pipeline, Executor executor, String name, Command command) {
        super(pipeline, executor, name);
        this.command = command;
    }

    @Override
    public Command command() {
        return this.command;
    }
}
