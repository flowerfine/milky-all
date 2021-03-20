package cn.sliew.milky.cache.ohc;

import cn.sliew.milky.cache.AbstractCacheFactory;
import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.cache.CacheOptions;

public class OhcCacheFactory extends AbstractCacheFactory {

    @Override
    protected Cache newCache(CacheOptions options) {
        return new OhcCache();
    }
}
