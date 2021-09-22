package cn.sliew.milky.thread;

import java.util.concurrent.BlockingQueue;

import static cn.sliew.milky.common.check.Ensures.checkArgument;

/**
 * adjust the capacity by a certain amount towards a maximum or minimum
 * by the {@code adjustCapacity} method.
 */
final class ResizableBlockingQueue<E> extends SizeBlockingQueue<E> {

    private volatile int capacity;

    ResizableBlockingQueue(BlockingQueue<E> queue, int initialCapacity) {
        super(queue, initialCapacity);
        this.capacity = initialCapacity;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int remainingCapacity() {
        return Math.max(0, this.capacity());
    }

    /**
     * Resize the limit for the queue, returning the new size limit
     */
    public synchronized int adjustCapacity(int optimalCapacity, int adjustmentAmount, int minCapacity, int maxCapacity) {
        checkArgument(adjustmentAmount > 0, () -> "adjustment amount should be a positive value");
        checkArgument(optimalCapacity > 0, () -> "desired capacity cannot be negative");
        checkArgument(minCapacity >= 0, () -> "cannot have min capacity smaller than 0");
        checkArgument(maxCapacity >= minCapacity, () -> "cannot have max capacity smaller than min capacity");

        if (optimalCapacity == capacity) {
            return this.capacity;
        }

        if (optimalCapacity > capacity + adjustmentAmount) {
            // adjust up
            final int newCapacity = Math.min(maxCapacity, capacity + adjustmentAmount);
            this.capacity = newCapacity;
            return newCapacity;
        } else if (optimalCapacity < capacity - adjustmentAmount) {
            // adjust down
            final int newCapacity = Math.max(minCapacity, capacity - adjustmentAmount);
            this.capacity = newCapacity;
            return newCapacity;
        } else {
            return this.capacity;
        }
    }
}