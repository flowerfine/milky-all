package cn.sliew.milky.common.diff;

import java.io.Serializable;

/**
 * States changes in which can be serialized
 */
public interface Diffable<T> extends Serializable {

    /**
     * Returns serializable object representing differences between this and previousState
     */
    Diff<T> diff(T previousState);

}
