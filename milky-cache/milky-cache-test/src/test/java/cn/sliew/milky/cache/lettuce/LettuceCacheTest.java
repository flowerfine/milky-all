package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.base.AbstractCacheTest;

public class LettuceCacheTest extends AbstractCacheTest {

    {
        LettuceCacheOptions options = new LettuceCacheOptions();
        options.name("LettuceCacheTest");
        options.host("localhost");
        options.port(6379);
        options.password("123");
        options.database(0);
        options.timeout(1000L);
        LettuceCacheFactory factory = new LettuceCacheFactory();
        this.cache = factory.getCache(options);
    }
}
