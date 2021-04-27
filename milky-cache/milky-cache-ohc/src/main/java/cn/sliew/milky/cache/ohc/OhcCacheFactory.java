package cn.sliew.milky.cache.ohc;

import cn.sliew.milky.cache.AbstractCacheFactory;
import cn.sliew.milky.cache.CacheOptions;

import static com.google.common.base.Preconditions.checkArgument;

public class OhcCacheFactory extends AbstractCacheFactory<OhcCache> {

    @Override
    protected OhcCache newCache(CacheOptions options) {
        checkArgument(options instanceof OhcCacheOptions);
        OhcCacheOptions ohcOptions = (OhcCacheOptions) options;
        return new OhcCache(ohcOptions);
    }
}
