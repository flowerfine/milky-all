package cn.sliew.milky.objectsize;

import java.io.Serializable;

public interface ObjectSizeOfStrategy {

    /**
     * Computes the size of an object. This typically is an estimation, not an absolute accurate size.
     *
     * @param obj The serializable object to compute size of
     * @return The size of the object in bytes. failed with -1.
     */
    long sizeOf(Serializable obj);

    long sizeOf(Object obj);
}
