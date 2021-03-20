package cn.sliew.milky.cache.ohc;

import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.common.cache.local.CacheLoader;
import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * todo 缓存是否支持null。
 */
public class OhcCache<K, V> implements Cache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(ProtostuffCacheSerializer.class);

    private final OHCache<K, V> ohc;

    public OhcCache() {
        ohc = OHCacheBuilder.<K, V>newBuilder()
//                .capacity(1024 * 1024 * 1024 * 2L)
//                .segmentCount(1024)
//                .maxEntrySize(1024 * 1024 * 1L)
                .eviction(Eviction.W_TINY_LFU)
                .defaultTTLmillis(1000 * 60L)
                .keySerializer(ProtostuffCacheSerializer.INSTANCE)
                .valueSerializer(ProtostuffCacheSerializer.INSTANCE)
                .timeouts(true)
//                .timeoutsSlots(64)
//                .timeoutsPrecision(128)
                .build();
    }

    @Override
    public String name() {
        return "ohc-cache";
    }

    @Override
    public V get(K key) {
        return ohc.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return ohc.containsKey(key);
    }

    @Override
    public V computeIfAbsent(K key, CacheLoader<K, V> loader, Duration expire) {
        try {
            return ohc.getWithLoader(key, key1 -> loader.load(key1), expire.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        ohc.put(key, value);
    }

    @Override
    public void put(K key, V value, Duration expire) {
        ohc.put(key, value, expire.toMillis() + System.currentTimeMillis());
    }

    @Override
    public void remove(K key) {
        ohc.remove(key);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        ohc.removeAll(keys);
    }

    @Override
    public long size() {
        return ohc.size();
    }

    @Override
    public Iterator<K> keyIterator() {
        return ohc.keyIterator();
    }

    @Override
    public Iterator<K> hotKeyIterator(int n) {
        return ohc.hotKeyIterator(n);
    }

    /**
     * todo 何时调用这个类
     */
    @Override
    public void stats(MeterRegistry registry) {
        OHCacheMeterBinder.monitor(registry, ohc, name());
    }

    @Override
    public void clear() {
        ohc.clear();
    }

    @Override
    public void destroy() {
        try {
            ohc.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
