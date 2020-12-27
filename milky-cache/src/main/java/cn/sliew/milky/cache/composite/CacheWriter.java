package cn.sliew.milky.cache.composite;

/**
 * Cache writer used for write-through operations.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface CacheWriter<K, V> {

    void write(K key, V value);

    void delete(K key);
}