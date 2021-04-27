package cn.sliew.milky.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCacheFactory<C extends Cache> implements CacheFactory {

    protected final Map<CacheOptions, C> map = new ConcurrentHashMap<>(16);

    @Override
    public C getCache(CacheOptions options) {
        C cache = map.get(options.getName());
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

    protected abstract C newCache(CacheOptions options);
}
