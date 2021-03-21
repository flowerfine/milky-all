package cn.sliew.milky.cache.ohc;

import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.cache.CacheLoader;
import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

/**
 * todo 缓存是否支持null。
 */
public class OhcCache<K, V> implements Cache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(ProtostuffCacheSerializer.class);

    private final OHCache<K, V> ohc;

    private final OhcCacheOptions<K, V> options;

    public OhcCache(OhcCacheOptions<K, V> options) {
        this.options = checkNotNull(options, "options can't be null");

        OHCacheBuilder<K, V> ohCacheBuilder = OHCacheBuilder.newBuilder();
        if (options.getSegmentCount() != null) {
            ohCacheBuilder.segmentCount(options.getSegmentCount());
        }
        if (options.getHashTableSize() != null) {
            ohCacheBuilder.hashTableSize(options.getHashTableSize());
        }
        if (options.getCapacity() != null) {
            ohCacheBuilder.capacity(options.getCapacity());
        }
        if (options.getChunkSize() != null) {
            ohCacheBuilder.chunkSize(options.getChunkSize());
        }
        ohCacheBuilder.keySerializer(options.getKeySerializer());
        ohCacheBuilder.valueSerializer(options.getValueSerializer());
        if (options.getLoadFactor() != null) {
            ohCacheBuilder.loadFactor(options.getLoadFactor());
        }
        if (options.getFixedKeySize() != null && options.getFixedValueSize() != null) {
            ohCacheBuilder.fixedEntrySize(options.getFixedKeySize(), options.getFixedValueSize());
        }
        if (options.getMaxEntrySize() != null) {
            ohCacheBuilder.maxEntrySize(options.getMaxEntrySize());
        }
        if (options.getThrowOOME() != null) {
            ohCacheBuilder.throwOOME(options.getThrowOOME());
        }
        if (options.getHashAlgorighm() != null) {
            ohCacheBuilder.hashMode(options.getHashAlgorighm());
        }
        if (options.getUnlocked() != null) {
            ohCacheBuilder.unlocked(options.getUnlocked());
        }
        if (options.getDefaultTTLmillis() != null) {
            ohCacheBuilder.defaultTTLmillis(options.getDefaultTTLmillis());
        }
        if (options.getTimeouts() != null) {
            ohCacheBuilder.timeouts(options.getTimeouts());
        }
        if (options.getTimeoutsSlots() != null) {
            ohCacheBuilder.timeoutsSlots(options.getTimeoutsSlots());
        }
        if (options.getTimeoutsPrecision() != null) {
            ohCacheBuilder.timeoutsPrecision(options.getTimeoutsPrecision());
        }
        if (options.getTicker() != null) {
            ohCacheBuilder.ticker(options.getTicker());
        }
        if (options.getEviction() != null) {
            ohCacheBuilder.eviction(options.getEviction());
        }
        if (options.getFrequencySketchSize() != null) {
            ohCacheBuilder.frequencySketchSize(options.getFrequencySketchSize());
        }
        if (options.getEdenSize() != null) {
            ohCacheBuilder.edenSize(options.getEdenSize());
        }
        this.ohc = ohCacheBuilder.build();
    }

    @Override
    public String name() {
        return this.options.getName();
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
