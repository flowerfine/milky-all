package cn.sliew.milky.common.recycler;

abstract class AbstractRecycler<T> implements Recycler<T> {

    protected final Source<T> source;

    protected AbstractRecycler(Source<T> source) {
        this.source = source;
    }
}
