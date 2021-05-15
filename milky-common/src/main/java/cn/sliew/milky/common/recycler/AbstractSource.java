package cn.sliew.milky.common.recycler;

public abstract class AbstractSource<T> implements Source<T> {

    @Override
    public abstract T newInstance();

    @Override
    public abstract void recycle(T value);
    
    @Override
    public void destroy(T value) {
        // by default we simply drop the object for GC.
    }
}
