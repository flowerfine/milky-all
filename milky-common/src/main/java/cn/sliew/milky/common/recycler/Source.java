package cn.sliew.milky.common.recycler;

public interface Source<T> {

    /**
     * Create a new empty instance of the given size.
     */
    T newInstance();

    /**
     * Recycle the data. This operation is called when the data structure is released.
     */
    void recycle(T value);

    /**
     * Destroy the data. This operation allows the data structure to release any internal resources before GC.
     */
    void destroy(T value);
}
