package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.CacheFactory;
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

    public LettuceCacheFactory() {
        RedisURI redisURI = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withPassword("123")
                .withDatabase(0)
                .withTimeout(Duration.ofSeconds(1L))
                .build();
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
