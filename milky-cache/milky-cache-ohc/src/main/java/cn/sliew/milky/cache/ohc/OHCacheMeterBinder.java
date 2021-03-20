package cn.sliew.milky.cache.ohc;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheStats;

import java.lang.ref.WeakReference;

public class OHCacheMeterBinder extends CacheMeterBinder {

    private final WeakReference<OHCache> cache;

    public OHCacheMeterBinder(OHCache cache, String cacheName, Iterable<Tag> tags) {
        super(cache, cacheName, tags);
        this.cache = new WeakReference(cache);
    }

    /**
     * Record metrics on an OHCache cache.
     *
     * @param registry The registry to bind metrics to.
     * @param cache    The cache to instrument.
     * @param tags     Tags to apply to all recorded metrics. Must be an even number of arguments representing key/value pairs of tags.
     * @return The instrumented cache, unchanged. The original cache is not wrapped or proxied in any way.
     */
    public static OHCache monitor(MeterRegistry registry, OHCache cache, String cacheName, String... tags) {
        return monitor(registry, cache, cacheName, Tags.of(tags));
    }

    /**
     * Record metrics on an OHCache cache.
     *
     * @param registry The registry to bind metrics to.
     * @param cache    The cache to instrument.
     * @param tags     Tags to apply to all recorded metrics.
     * @return The instrumented cache, unchanged. The original cache is not wrapped or proxied in any way.
     */
    public static OHCache monitor(MeterRegistry registry, OHCache cache, String cacheName, Iterable<Tag> tags) {
        new OHCacheMeterBinder(cache, cacheName, tags).bindTo(registry);
        return cache;
    }

    @Override
    protected Long size() {
        return this.cache.get().stats().getSize();
    }

    @Override
    protected long hitCount() {
        return this.cache.get().stats().getHitCount();
    }

    @Override
    protected Long missCount() {
        return this.cache.get().stats().getMissCount();
    }

    @Override
    protected Long evictionCount() {
        return this.cache.get().stats().getEvictionCount();
    }

    @Override
    protected long putCount() {
        OHCacheStats stats = this.cache.get().stats();
        return stats.getPutAddCount() + stats.getPutReplaceCount();
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
        FunctionCounter.builder("cache.put", cache.get().stats(), c -> c.getPutAddCount())
                .tags(getTagsWithCacheName())
                .tag("result", "add")
                .description("Cache put add count")
                .register(registry);
        FunctionCounter.builder("cache.put", cache.get().stats(), c -> c.getPutReplaceCount())
                .tags(getTagsWithCacheName())
                .tag("result", "replace")
                .description("Cache put replace count")
                .register(registry);
        FunctionCounter.builder("cache.put", cache.get().stats(), c -> c.getPutFailCount())
                .tags(getTagsWithCacheName())
                .tag("result", "fail")
                .description("Cache put fail count")
                .register(registry);
        FunctionCounter.builder("cache.expire", cache.get().stats(), c -> c.getExpireCount())
                .tags(getTagsWithCacheName())
                .description("Cache expire count")
                .register(registry);
        FunctionCounter.builder("cache.remove", cache.get().stats(), c -> c.getRemoveCount())
                .tags(getTagsWithCacheName())
                .description("Cache remove count")
                .register(registry);
        FunctionCounter.builder("cache.rehash", cache.get().stats(), c -> c.getRemoveCount())
                .tags(getTagsWithCacheName())
                .description("Cache rehash count")
                .register(registry);

        Gauge.builder("cache.offheap.size", cache.get().stats(), c -> c.getCapacity())
                .tags(getTagsWithCacheName())
                .tag("offheap", "capacity")
                .description("Cache offheap capacity")
                .register(registry);
        Gauge.builder("cache.offheap.size", cache.get().stats(), c -> c.getFree())
                .tags(getTagsWithCacheName())
                .tag("offheap", "free")
                .description("Cache offheap free")
                .register(registry);
        Gauge.builder("cache.offheap.size", cache.get().stats(), c -> c.getTotalAllocated())
                .tags(getTagsWithCacheName())
                .tag("offheap", "totalAllocated")
                .description("Cache offheap totalAllocated")
                .register(registry);
        Gauge.builder("cache.lruCompactions", cache.get().stats(), c -> c.getLruCompactions())
                .tags(getTagsWithCacheName())
                .description("Cache lruCompactions. OHCache always 0.")
                .register(registry);
    }
}