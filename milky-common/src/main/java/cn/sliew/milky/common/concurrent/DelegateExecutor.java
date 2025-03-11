package cn.sliew.milky.common.concurrent;

import java.util.concurrent.Executor;

public class DelegateExecutor<T extends Executor> implements Executor {

    protected T executor;

    public DelegateExecutor(T executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }
}
