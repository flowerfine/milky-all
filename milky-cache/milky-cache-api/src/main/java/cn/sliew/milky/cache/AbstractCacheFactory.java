package cn.sliew.milky.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCacheFactory implements CacheFactory {

    protected final Map<CacheOptions, Cache> map = new ConcurrentHashMap<>(16);

    @Override
    public <K, V> Cache<K, V> getCache(CacheOptions options) {
        Cache<K, V> cache = map.get(options.getName());
        if (cache == null) {
            synchronized (map) {
                cache = map.get(options);
                if (cache == null) {
                    cache = newCache(options);
                    map.put(options, cache);
                }
            }
        }
        return cache;
    }

    protected abstract Cache newCache(CacheOptions options);
}
