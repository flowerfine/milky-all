package cn.sliew.milky.cache.composite;

import cn.sliew.milky.cache.local.CacheLoader;

import java.util.concurrent.ExecutionException;

public interface Cache<K, V> {

    V get(K key);

    V computeIfAbsent(K key, CacheLoader<K, V> loader) throws ExecutionException;

    void put(K key, V value);

    void invalidate(K key);
}