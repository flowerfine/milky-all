package cn.sliew.milky.cache;

import io.micrometer.core.instrument.MeterRegistry;

import java.time.Duration;
import java.util.Iterator;

/**
 * todo 异步功能支持
 */
public interface Cache<K, V> {

    String name();

    V get(K key);

    boolean containsKey(K key);

    V computeIfAbsent(K key, CacheLoader<K, V> loader, Duration expire);

    void put(K key, V value);

    void put(K key, V value, Duration expire);

    void remove(K key);

    void removeAll(Iterable<K> keys);

    long size();

    Iterator<K> keyIterator();

    Iterator<K> hotKeyIterator(int n);

    /**
     * 通过MeterBinder进行暴露
     */
    void stats(MeterRegistry registry);

    void clear();

    void destroy();
}