package cn.sliew.milky.cache.caffeine;

import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.cache.CacheLoader;
import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

/**
 * refresh > expire ensure hot key would refreshed before expired.
 * cache key would not be refreshed when no request comes.
 *
 * because maximumSize can't be combined with maximumWeight, CaffeineCache
 * would not support maximumWeight parameter.
 */
public class CaffeineCache<K, V> implements Cache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CaffeineCache.class);

    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    private final CaffeineCacheOptions<K, V> options;

    public CaffeineCache(CaffeineCacheOptions<K, V> options) {
        this.options = checkNotNull(options, "options can't be null");

        Caffeine<K, V> caffeine = (Caffeine<K, V>) Caffeine.newBuilder()
                .initialCapacity(options.getInitialCapacity())
                .maximumSize(options.getMaximumSize())
                .expireAfterAccess(options.getExpireAfterAccess())
                .expireAfterWrite(options.getExpireAfterWrite())
                .recordStats();
        if (options.isWeakKeys()) {
            caffeine.weakKeys();
        }
        if (options.isWeakValues()) {
            caffeine.weakValues();
        }
        if (options.isSoftValues()) {
            caffeine.softValues();
        }
        // refreshAfterWrite requires a LoadingCache
        if (options.getLoader() != null) {
            caffeine.refreshAfterWrite(options.getRefreshAfterWrite());
            this.cache = caffeine.build(new com.github.benmanes.caffeine.cache.CacheLoader<K, V>() {
                @Override
                public @Nullable V load(K key) throws Exception {
                    return options.getLoader().load(key);
                }
            });
        } else {
            this.cache = caffeine.build();
        }
    }

    @Override
    public String name() {
        return this.options.getName();
    }

    @Override
    public V get(K key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public V computeIfAbsent(K key, CacheLoader<K, V> loader, Duration expire) {
        V value = get(key);
        if (value == null) {
            synchronized (cache) {
                value = get(key);
                if (value == null) {
                    try {
                        value = loader.load(key);
                        put(key, value, expire);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        return value;
    }

    @Override
    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    @Override
    public void put(K key, V value, Duration expire) {
        this.cache.put(key, value);
    }

    @Override
    public void remove(K key) {
        this.cache.invalidate(key);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        this.cache.invalidateAll(keys);
    }

    @Override
    public long size() {
        return this.cache.estimatedSize();
    }

    @Override
    public boolean supportNullKey() {
        return false;
    }

    @Override
    public boolean supportNullValue() {
        return false;
    }

    @Override
    public Iterator<K> keyIterator() {
        return this.cache.asMap().keySet().iterator();
    }

    @Override
    public Iterator<K> hotKeyIterator(int n) {
        return Collections.emptyIterator();
    }

    @Override
    public void stats(MeterRegistry registry) {
        CaffeineCacheMetrics.monitor(registry, this.cache, this.name());
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    @Override
    public void destroy() {

    }
}
