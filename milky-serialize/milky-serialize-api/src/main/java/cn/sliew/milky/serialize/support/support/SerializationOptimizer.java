package cn.sliew.milky.serialize.support.support;

import java.util.Collection;

/**
 * Interface defining serialization optimizer, there are nothing implementations for now.
 */
public interface SerializationOptimizer {

    /**
     * Get serializable classes
     *
     * @return serializable classes
     */
    Collection<Class<?>> getSerializableClasses();
}
