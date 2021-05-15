package cn.sliew.milky.common.recycler;

public interface Recycler<T> {

    Value<T> obtain();
}
