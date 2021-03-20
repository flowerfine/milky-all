package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.CacheFactory;
import cn.sliew.milky.common.util.StringUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class LettuceCacheFactory  implements CacheFactory {

    private Map<String, LettuceCache> map = new HashMap<>(16);

    private final StatefulRedisConnection connection;

    public LettuceCacheFactory(LettuceCacheOptions options) {
        RedisURI redisURI = RedisURI.builder()
                .withHost(options.getHost())
                .withPort(options.getPort())
                .withDatabase(options.getDatabase())
                .withTimeout(Duration.ofMillis(options.getTimeout()))
                .build();
        if (StringUtils.isNotBlank(options.getPassword())) {
            redisURI.setPassword(options.getPassword());
        }
        this.connection = RedisClient.create(redisURI).connect(ProtostuffCodec.INSTANCE);
    }

    @Override
    public <K, V> LettuceCache<K, V> getCache(String name) {
        checkArgument(name != null && name.length() > 0, "name cant be empty");
        LettuceCache<K, V> cache = map.get(name);
        if (cache == null) {
            synchronized (map) {
                cache = map.get(name);
                if (cache == null) {
                    cache = new LettuceCache<>(connection);
                    map.put(name, cache);
                }
            }
        }
        return cache;
    }
}
