package cn.sliew.milky.common.chain;

import cn.sliew.milky.log.Logger;

import java.util.Map;

/**
 * 执行上下文，用来在不同的{@link Command}间传递信息。
 * Map后面可以考虑使用AttributeMap代替。
 */
public interface Context<K, V> extends Map<K, V> {

    /**
     * That method enhances the {@link #get(Object)} method that helps users
     * avoid the redundant code of type cast/checking when assignments are already known.
     * <p>
     * It throws {@code ClassCastException} if types are not assignable.
     *
     * @param <T> the target assignment type
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key
     * @see #get(Object)
     */
    <T extends V> T retrieve(K key);

    /**
     * This method provider a {@code Logger} utililies for user can't construct a logger
     * or try to log pipeline context message to one logger file.
     *
     * @return logger
     */
    Logger logger();
}
