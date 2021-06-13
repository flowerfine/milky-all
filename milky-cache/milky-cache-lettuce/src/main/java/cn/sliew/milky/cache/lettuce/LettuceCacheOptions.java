package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.CacheOptions;
import io.lettuce.core.RedisURI;

import java.util.List;
import java.util.Objects;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.notEmpty;

public class LettuceCacheOptions<K, V> extends CacheOptions<K, V> {

    private RedisURI redisURI;

    private List<RedisURI> clusterRedisURIS;

    public LettuceCacheOptions() {
        super();
    }

    /**
     * Sets {@code redisURI}.
     *
     * @param redisURI redisURI
     * @return LettuceCacheOptions instance
     */
    public void redisURI(RedisURI redisURI) {
        checkNotNull(redisURI, () -> "lettuce redisURI can't be empty");
        this.redisURI = redisURI;
    }

    public RedisURI getRedisURI() {
        return redisURI;
    }

    public void clusterRedisURIS(List<RedisURI> clusterRedisURIS) {
        notEmpty(clusterRedisURIS, () -> "lettuce clusterRedisURIS can't be empty");
        this.clusterRedisURIS = clusterRedisURIS;
    }

    public List<RedisURI> getClusterRedisURIS() {
        return clusterRedisURIS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LettuceCacheOptions<?, ?> that = (LettuceCacheOptions<?, ?>) o;
        return Objects.equals(redisURI, that.redisURI) &&
                Objects.equals(clusterRedisURIS, that.clusterRedisURIS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), redisURI, clusterRedisURIS);
    }
}
