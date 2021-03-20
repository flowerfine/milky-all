package cn.sliew.milky.cache;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.AbstractCache;
import com.google.common.cache.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class CacheBuilder<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheBuilder.class);

    int initialCapacity = 16;

    int concurrencyLevel = Runtime.getRuntime().availableProcessors();
    long maximumSize = 16;

    long expireAfterWriteNanos = Duration.ofMinutes(1).toNanos();

    long expireAfterAccessNanos = Duration.ofMinutes(1).toNanos();

    long refreshNanos = Duration.ofMinutes(1).toNanos();

    static final Supplier<? extends AbstractCache.StatsCounter> NULL_STATS_COUNTER =
            Suppliers.ofInstance(
                    new AbstractCache.StatsCounter() {
                        @Override
                        public void recordHits(int count) {
                        }

                        @Override
                        public void recordMisses(int count) {
                        }

                        @SuppressWarnings("GoodTime") // b/122668874
                        @Override
                        public void recordLoadSuccess(long loadTime) {
                        }

                        @SuppressWarnings("GoodTime") // b/122668874
                        @Override
                        public void recordLoadException(long loadTime) {
                        }

                        @Override
                        public void recordEviction() {
                        }

                        @Override
                        public CacheStats snapshot() {
                            return EMPTY_STATS;
                        }
                    });

    static final CacheStats EMPTY_STATS = new CacheStats(0, 0, 0, 0, 0, 0);

    static final Supplier<AbstractCache.StatsCounter> CACHE_STATS_COUNTER =
            new Supplier<AbstractCache.StatsCounter>() {
                @Override
                public AbstractCache.StatsCounter get() {
                    return new AbstractCache.SimpleStatsCounter();
                }
            };

    private CacheBuilder() {

    }

    public static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
    }

    public CacheBuilder<K, V> initialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public CacheBuilder<K, V> maximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public CacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit) {
        this.expireAfterWriteNanos = unit.toNanos(duration);
        return this;
    }

    public CacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit) {
        this.expireAfterAccessNanos = unit.toNanos(duration);
        return this;
    }

    public CacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit) {
        this.refreshNanos = unit.toNanos(duration);
        return this;
    }
}
