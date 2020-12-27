package cn.sliew.milky.cache.composite;

public class LocalCacheOptions<K, V> extends CacheOptions<K, V> {

    public enum EvictionPolicy {

        /**
         * Cache without eviction.
         */
        NONE,

        /**
         * Least Recently Used cache.
         */
        LRU,

        /**
         * Least Frequently Used cache.
         */
        LFU,

        /**
         * Cache with Soft Reference used for values.
         * All references will be collected by GC
         */
        SOFT,

        /**
         * Cache with Weak Reference used for values.
         * All references will be collected by GC
         */
        WEAK
    }

    ;

    private EvictionPolicy evictionPolicy;

    private int cacheSize;


    private long timeToLiveInMillis;
    private long maxIdleInMillis;

    protected LocalCacheOptions() {
    }

    /**
     * Sets eviction policy.
     *
     * @param evictionPolicy <p><code>LRU</code> - uses cache with LRU (least recently used) eviction policy.
     *                       <p><code>LFU</code> - uses cache with LFU (least frequently used) eviction policy.
     *                       <p><code>SOFT</code> - uses cache with soft references. The garbage collector will evict items from the cache when the JVM is running out of memory.
     *                       <p><code>NONE</code> - doesn't use eviction policy, but timeToLive and maxIdleTime params are still working.
     * @return LocalCachedMapOptions instance
     */
    public LocalCacheOptions<K, V> evictionPolicy(EvictionPolicy evictionPolicy) {
        if (evictionPolicy == null) {
            throw new NullPointerException("evictionPolicy can't be null");
        }
        this.evictionPolicy = evictionPolicy;
        return this;
    }

    public EvictionPolicy getEvictionPolicy() {
        return evictionPolicy;
    }

    /**
     * Sets cache size. If size is <code>0</code> then local cache is unbounded.
     *
     * @param cacheSize - size of cache
     * @return LocalCachedMapOptions instance
     */
    public LocalCacheOptions<K, V> cacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }

    public int getCacheSize() {
        return cacheSize;
    }


    @Override
    public LocalCacheOptions<K, V> writer(CacheWriter<K, V> writer) {
        return (LocalCacheOptions<K, V>) super.writer(writer);
    }

    @Override
    public LocalCacheOptions<K, V> writeBehindThreads(int writeBehindThreads) {
        return (LocalCacheOptions<K, V>) super.writeBehindThreads(writeBehindThreads);
    }

    @Override
    public LocalCacheOptions<K, V> writeMode(CacheOptions.WriteMode writeMode) {
        return (LocalCacheOptions<K, V>) super.writeMode(writeMode);
    }

    @Override
    public LocalCacheOptions<K, V> loader(CacheLoader<K, V> loader) {
        return (LocalCacheOptions<K, V>) super.loader(loader);
    }
}
