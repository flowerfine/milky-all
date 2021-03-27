package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.CacheOptions;

import java.util.Objects;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class LettuceCacheOptions<K, V> extends CacheOptions<K, V> {

    private String redisURI;

    public LettuceCacheOptions() {
        super();
    }

    /**
     * Sets {@code redisURI}.
     *
     * @param redisURI redisURI
     * @return LettuceCacheOptions instance
     */
    public void redisURI(String redisURI) {
        notBlank(redisURI, "lettuce redisURI can't be empty");
        this.redisURI = redisURI;
    }

    public String getRedisURI() {
        return redisURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LettuceCacheOptions<?, ?> that = (LettuceCacheOptions<?, ?>) o;
        return Objects.equals(redisURI, that.redisURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), redisURI);
    }
}
