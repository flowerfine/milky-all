package cn.sliew.milky.cache.caffeine;

import cn.sliew.milky.cache.base.AbstractCacheTest;

public class CaffeineCacheTest extends AbstractCacheTest {

    {
        CaffeineCacheOptions options = new CaffeineCacheOptions();
        options.name("CaffeineCacheTest");
        CaffeineCacheFactory factory = new CaffeineCacheFactory();
        cache = factory.getCache(options);
    }
}
