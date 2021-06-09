package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.base.AbstractCacheTest;
import io.lettuce.core.RedisURI;

import java.time.Duration;

public class LettuceCacheTest extends AbstractCacheTest {

    {
        LettuceCacheOptions options = new LettuceCacheOptions();
        options.name("LettuceCacheTest");
        RedisURI redisURI = RedisURI.builder().withHost("localhost").withPort(6379).withPassword("123").withDatabase(0).withTimeout(Duration.ofSeconds(1L)).build();
        options.redisURI(redisURI);
        LettuceCacheFactory factory = new LettuceCacheFactory();
        this.cache = factory.getCache(options);
    }

}
