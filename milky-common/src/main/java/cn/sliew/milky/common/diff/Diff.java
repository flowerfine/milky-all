package cn.sliew.milky.common.diff;

import java.io.Serializable;

/**
 * Represents difference between states.
 */
public interface Diff<T> extends Serializable {

    /**
     * Applies difference to the specified part and returns the resulted part
     */
    T apply(T part);
}