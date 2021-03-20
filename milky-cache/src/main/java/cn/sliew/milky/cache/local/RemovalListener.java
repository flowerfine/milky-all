package cn.sliew.milky.cache.local;

@FunctionalInterface
public interface RemovalListener<K, V> {
    void onRemoval(RemovalNotification<K, V> notification);
}