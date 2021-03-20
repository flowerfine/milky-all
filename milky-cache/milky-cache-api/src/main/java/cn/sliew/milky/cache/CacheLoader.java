package cn.sliew.milky.cache;

import java.util.concurrent.CompletableFuture;

public interface CacheLoader<K, V> {

    V load(K key) throws Exception;

    default CompletableFuture<V> reload(K key, V oldValue) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return load(key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
