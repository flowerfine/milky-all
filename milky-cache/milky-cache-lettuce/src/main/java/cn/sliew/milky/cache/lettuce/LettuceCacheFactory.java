package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.AbstractCacheFactory;
import cn.sliew.milky.cache.CacheOptions;

import static cn.sliew.milky.common.check.Ensures.checkArgument;

public class LettuceCacheFactory extends AbstractCacheFactory<LettuceCache> {

    @Override
    protected LettuceCache newCache(CacheOptions options) {
        checkArgument(options instanceof LettuceCacheOptions);

        LettuceCacheOptions lettuceOptions = (LettuceCacheOptions) options;
        return new LettuceCache(lettuceOptions);
    }

}
