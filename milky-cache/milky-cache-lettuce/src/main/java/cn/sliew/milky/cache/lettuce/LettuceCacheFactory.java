package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.AbstractCacheFactory;
import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.cache.CacheOptions;
import cn.sliew.milky.common.util.StringUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;

public class LettuceCacheFactory extends AbstractCacheFactory {

    @Override
    protected Cache newCache(CacheOptions options) {
        checkArgument(options instanceof LettuceCacheOptions);
        LettuceCacheOptions lettuce = (LettuceCacheOptions) options;
        RedisURI redisURI = RedisURI.builder()
                .withHost(lettuce.getHost())
                .withPort(lettuce.getPort())
                .withDatabase(lettuce.getDatabase())
                .withTimeout(Duration.ofMillis(lettuce.getTimeout()))
                .build();
        if (StringUtils.isNotBlank(lettuce.getPassword())) {
            redisURI.setPassword(lettuce.getPassword());
        }
        StatefulRedisConnection connection = RedisClient.create(redisURI).connect(ProtostuffCodec.INSTANCE);
        return new LettuceCache(connection);
    }
}
