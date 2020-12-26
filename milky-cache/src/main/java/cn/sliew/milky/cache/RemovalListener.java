package cn.sliew.milky.cache;

@FunctionalInterface
public interface RemovalListener<K, V> {
    void onRemoval(RemovalNotification<K, V> notification);
}