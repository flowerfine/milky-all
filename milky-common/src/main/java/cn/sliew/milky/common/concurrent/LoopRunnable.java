package cn.sliew.milky.common.concurrent;

public interface LoopRunnable extends Runnable {

    void execute();

    void terminate();

    boolean isTerminated();

    @Override
    default void run() {
        while (isTerminated() == false) {
            execute();
        }
    }
}
