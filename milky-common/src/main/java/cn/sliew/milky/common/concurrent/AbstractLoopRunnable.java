package cn.sliew.milky.common.concurrent;

public abstract class AbstractLoopRunnable implements LoopRunnable {

    protected volatile boolean terminal = false;

    @Override
    public void terminate() {
        terminal = true;
    }

    @Override
    public boolean isTerminated() {
        return terminal;
    }
}
