package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.base.AbstractCacheTest;
import io.lettuce.core.RedisURI;

import java.time.Duration;
import java.util.Arrays;

public class LettuceCacheTest extends AbstractCacheTest {

    {
        LettuceCacheOptions options = new LettuceCacheOptions();
        options.name("LettuceCacheTest");
        RedisURI redisURI = RedisURI.builder()
                .withHost("dev-cluster-common.redis.rds.aliyuncs.com")
                .withPort(6379)
                .withPassword("clustDerdeFvr4ed9isM")
                .withDatabase(0)
                .withTimeout(Duration.ofSeconds(1L))
                .build();
        options.clusterRedisURIS(Arrays.asList(redisURI));
//        options.redisURI(redisURI);
        LettuceCacheFactory factory = new LettuceCacheFactory();
        this.cache = factory.getCache(options);
    }

}
