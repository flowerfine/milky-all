package cn.sliew.milky.cache;

@FunctionalInterface
public interface CacheLoader<K, V> {
    V load(K key) throws Exception;
}
