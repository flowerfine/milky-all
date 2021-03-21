package cn.sliew.milky.cache.caffeine;

import cn.sliew.milky.cache.CacheOptions;

import java.time.Duration;

import static cn.sliew.milky.common.check.Ensures.checkArgument;
import static cn.sliew.milky.common.check.Ensures.checkNotNull;

/**
 * {@code recordStats} default {@code true}, can't be disabled.
 *
 * @see CaffeineSpec
 */
public class CaffeineCacheOptions<K, V> extends CacheOptions<K, V> {

    private Integer initialCapacity = 4;

    private Long maximumSize = 20L;

    private Long maximumWeight = 0L;

    private Boolean weakKeys = false;

    private Boolean weakValues = false;

    private Boolean softValues = false;

    private Duration expireAfterAccess = Duration.ofSeconds(60L);

    private Duration expireAfterWrite = Duration.ofSeconds(45L);

    private Duration refreshAfterWrite = Duration.ofSeconds(30L);

    public CaffeineCacheOptions() {
        super();
    }

    /**
     * Sets cache {@code initialCapacity}.
     *
     * @param initialCapacity cache initialCapacity
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> initialCapacity(int initialCapacity) {
        checkArgument(initialCapacity > 0, String.format("cache initialCapacity invalid: %d", initialCapacity));
        this.initialCapacity = initialCapacity;
        return this;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    /**
     * Sets cache {@code maximumSize}.
     *
     * @param maximumSize cache maximumSize
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> maximumSize(long maximumSize) {
        checkArgument(maximumSize > 0, String.format("cache maximumSize invalid: %d", maximumSize));
        this.maximumSize = maximumSize;
        return this;
    }

    public long getMaximumSize() {
        return maximumSize;
    }

    /**
     * Sets cache {@code maximumWeight}.
     *
     * @param maximumWeight cache maximumWeight
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> maximumWeight(long maximumWeight) {
        checkArgument(maximumWeight > 0, String.format("cache maximumWeight invalid: %d", maximumWeight));
        this.maximumWeight = maximumWeight;
        return this;
    }

    public long getMaximumWeight() {
        return maximumWeight;
    }

    /**
     * Sets cache {@code weakKeys}.
     *
     * @param weakKeys cache weak key.
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> weakKeys(boolean weakKeys) {
        this.weakKeys = weakKeys;
        return this;
    }

    public boolean isWeakKeys() {
        return weakKeys;
    }

    /**
     * Sets cache {@code weakValues}.
     *
     * @param weakValues cache weak value.
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> weakValues(boolean weakValues) {
        this.weakValues = weakValues;
        return this;
    }

    public boolean isWeakValues() {
        return weakValues;
    }

    /**
     * Sets cache {@code softValues}.
     *
     * @param softValues cache soft value.
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> softValues(boolean softValues) {
        this.softValues = softValues;
        return this;
    }

    public boolean isSoftValues() {
        return softValues;
    }

    /**
     * Sets cache {@code expireAfterAccess}.
     *
     * @param expireAfterAccess duration expire after cache access.
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> expireAfterAccess(Duration expireAfterAccess) {
        checkNotNull(expireAfterAccess, "cache refreshAfterWrite can't be null");
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    public Duration getExpireAfterAccess() {
        return expireAfterAccess;
    }

    /**
     * Sets cache {@code expireAfterWrite}.
     *
     * @param expireAfterWrite duration expire after cache write.
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> expireAfterWrite(Duration expireAfterWrite) {
        checkNotNull(expireAfterWrite, "cache expireAfterWrite can't be null");
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    public Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }

    /**
     * Sets cache {@code refreshAfterWrite}.
     *
     * @param refreshAfterWrite duration refresh after cache write.
     * @return CaffeineCacheOptions instance
     */
    public CaffeineCacheOptions<K, V> refreshAfterWrite(Duration refreshAfterWrite) {
        checkNotNull(refreshAfterWrite, "cache refreshAfterWrite can't be null");
        this.refreshAfterWrite = refreshAfterWrite;
        return this;
    }

    public Duration getRefreshAfterWrite() {
        return refreshAfterWrite;
    }

}
