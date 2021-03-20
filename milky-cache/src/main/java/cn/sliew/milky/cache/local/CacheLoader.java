package cn.sliew.milky.cache.local;

@FunctionalInterface
public interface CacheLoader<K, V> {
    V load(K key) throws Exception;
}
