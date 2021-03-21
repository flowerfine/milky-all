package cn.sliew.milky.common.concurrent;

import cn.sliew.milky.common.check.Assertions;
import cn.sliew.milky.common.exception.BizException;
import cn.sliew.milky.common.release.Releasable;

import java.util.concurrent.locks.Lock;

/**
 * Releasable lock used inside of Engine implementations
 */
public class ReleasableLock implements Releasable {
    private final Lock lock;


    // a per-thread count indicating how many times the thread has entered the lock; only works if assertions are enabled
    private final ThreadLocal<Integer> holdingThreads;

    public ReleasableLock(Lock lock) {
        this.lock = lock;
        if (Assertions.ENABLED) {
            holdingThreads = new ThreadLocal<>();
        } else {
            holdingThreads = null;
        }
    }

    @Override
    public void close() {
        lock.unlock();
        assert removeCurrentThread();
    }


    public ReleasableLock acquire() throws BizException {
        lock.lock();
        assert addCurrentThread();
        return this;
    }

    private boolean addCurrentThread() {
        final Integer current = holdingThreads.get();
        holdingThreads.set(current == null ? 1 : current + 1);
        return true;
    }

    private boolean removeCurrentThread() {
        final Integer count = holdingThreads.get();
        assert count != null && count > 0;
        if (count == 1) {
            holdingThreads.remove();
        } else {
            holdingThreads.set(count - 1);
        }
        return true;
    }

    public boolean isHeldByCurrentThread() {
        if (holdingThreads == null) {
            throw new UnsupportedOperationException("asserts must be enabled");
        }
        final Integer count = holdingThreads.get();
        return count != null && count > 0;
    }
}
