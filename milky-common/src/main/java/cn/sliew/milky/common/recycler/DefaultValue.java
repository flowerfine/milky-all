package cn.sliew.milky.common.recycler;

public class DefaultValue<T> implements Value<T> {

    @Override
    public T value() {
        return null;
    }

    @Override
    public boolean isRecycled() {
        return false;
    }

    @Override
    public void close() {

    }
}
