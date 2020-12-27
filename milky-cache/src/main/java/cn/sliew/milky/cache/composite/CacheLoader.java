package cn.sliew.milky.cache.composite;

import java.util.concurrent.CompletableFuture;

public abstract class CacheLoader<K, V> {

    protected CacheLoader() {
    }

    public abstract V load(K key) throws Exception;

    public CompletableFuture<V> reload(K key, V oldValue) throws Exception {
        return CompletableFuture.completedFuture(load(key));
    }
}
