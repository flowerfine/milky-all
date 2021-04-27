package cn.sliew.milky.cache.caffeine;

import cn.sliew.milky.cache.AbstractCacheFactory;
import cn.sliew.milky.cache.CacheOptions;

import static com.google.common.base.Preconditions.checkArgument;

public class CaffeineCacheFactory extends AbstractCacheFactory<CaffeineCache> {

    @Override
    protected CaffeineCache newCache(CacheOptions options) {
        checkArgument(options instanceof CaffeineCacheOptions);
        CaffeineCacheOptions caffeineOptions = (CaffeineCacheOptions) options;
        return new CaffeineCache(caffeineOptions);
    }
}
