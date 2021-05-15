package cn.sliew.milky.common.recycler;

public interface Value<V> {

    V value();

    boolean isRecycled();

    void close();
}
