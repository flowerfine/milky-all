package cn.sliew.milky.common.recycler;

import java.util.Deque;

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
        return null;
    }
}
