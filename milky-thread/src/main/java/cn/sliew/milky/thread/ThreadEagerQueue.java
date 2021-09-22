package cn.sliew.milky.thread;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;

class ThreadEagerQueue<E> extends LinkedTransferQueue<E> {

    private final ThreadPoolExecutor executor;

    ThreadEagerQueue(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public boolean offer(E e) {
        if (!tryTransfer(e)) {
            if (executor.getMaximumPoolSize() > executor.getCorePoolSize()) {
                return false;
            }
            return super.offer(e);
        }
        return true;
    }
}
