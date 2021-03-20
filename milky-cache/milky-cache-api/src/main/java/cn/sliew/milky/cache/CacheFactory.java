package cn.sliew.milky.cache;

public interface CacheFactory {

    <K, V> Cache<K, V> getCache(CacheOptions options);
}