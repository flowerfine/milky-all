package cn.sliew.milky.common.recycler;

import java.util.Deque;

/**
 * A {@link Recycler} implementation based on a {@link Deque}. This implementation is NOT thread-safe.
 */
public class DequeRecycler<T> extends AbstractRecycler<T> {

    final Deque<T> deque;
    final int maxSize;

    public DequeRecycler(Source<T> source, Deque<T> deque, int maxSize) {
        super(source);
        this.deque = deque;
        this.maxSize = maxSize;
    }

    @Override
    public Value<T> obtain() {
        final T v = deque.pollFirst();
        if (v == null) {
            return new DefaultValue(source.newInstance(), false);
        }
        return new DefaultValue(v, true);
    }

    /**
     * Called before releasing an object, returns true if the object should be recycled and false otherwise.
     */
    protected boolean beforeRelease() {
        return deque.size() < maxSize;
    }

    /**
     * Called after a release.
     */
    protected void afterRelease(boolean recycled) {
        // nothing to do
    }


    private class DefaultValue implements Value<T> {

        T value;
        final boolean recycled;

        DefaultValue(T value, boolean recycled) {
            this.value = value;
            this.recycled = recycled;
        }

        @Override
        public T value() {
            return this.value;
        }

        @Override
        public boolean isRecycled() {
            return this.recycled;
        }

        @Override
        public void close() {
            if (value == null) {
                throw new IllegalStateException("recycler entry already released...");
            }
            final boolean recycle = beforeRelease();
            if (recycle) {
                source.recycle(value);
                deque.addFirst(value);
            } else {
                source.destroy(value);
            }
            value = null;
            afterRelease(recycle);
        }
    }
}
