package cn.sliew.milky.common.cache.local;

@FunctionalInterface
public interface CacheLoader<K, V> {
    V load(K key) throws Exception;
}
