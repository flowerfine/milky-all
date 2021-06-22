package cn.sliew.milky.common.recycler;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link Recycler} implementation based on a concurrent {@link Deque}. This implementation is thread-safe.
 */
public class ConcurrentDequeRecycler<T> extends DequeRecycler<T> {

    // we maintain size separately because concurrent deque implementations typically have linear-time size() impls
    final AtomicInteger size;

    public ConcurrentDequeRecycler(Source<T> source, int maxSize) {
        super(source, new ConcurrentLinkedDeque<>(), maxSize);
        this.size = new AtomicInteger();
    }

    @Override
    public Value<T> obtain() {
        final Value<T> v = super.obtain();
        if (v.isRecycled()) {
            size.decrementAndGet();
        }
        return v;
    }

    @Override
    protected boolean beforeRelease() {
        return size.incrementAndGet() <= maxSize;
    }

    @Override
    protected void afterRelease(boolean recycled) {
        if (!recycled) {
            size.decrementAndGet();
        }
    }
}
