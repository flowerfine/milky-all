package cn.sliew.milky.cache.ohc;

import cn.sliew.milky.cache.CacheOptions;
import org.caffinitas.ohc.CacheSerializer;
import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.HashAlgorithm;
import org.caffinitas.ohc.Ticker;

import java.util.Objects;

import static cn.sliew.milky.common.check.Ensures.checkArgument;
import static cn.sliew.milky.common.check.Ensures.checkNotNull;

public class OhcCacheOptions<K, V> extends CacheOptions<K, V> {

    private Integer segmentCount;
    private Integer hashTableSize = 8192;
    private Long capacity;
    private Integer chunkSize;
    private CacheSerializer<K> keySerializer = ProtostuffCacheSerializer.INSTANCE;
    private CacheSerializer<V> valueSerializer = ProtostuffCacheSerializer.INSTANCE;
    private Float loadFactor = .75f;
    private Integer fixedKeySize;
    private Integer fixedValueSize;
    private Long maxEntrySize;
    private Boolean throwOOME;
    private HashAlgorithm hashAlgorighm = HashAlgorithm.MURMUR3;
    private Boolean unlocked;
    private Long defaultTTLmillis = 1000 * 60L;
    private Boolean timeouts = true;
    private Integer timeoutsSlots;
    private Integer timeoutsPrecision;
    private Ticker ticker = Ticker.DEFAULT;
    private Eviction eviction = Eviction.W_TINY_LFU;
    private Integer frequencySketchSize;
    private Double edenSize = 0.2d;

    public OhcCacheOptions() {
        super();
    }

    /**
     * Sets cache number of segments.
     * default 2 * number of CPUs ({@code java.lang.Runtime.availableProcessors()}).
     *
     * @param segmentCount cache number of segments
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> segmentCount(int segmentCount) {
        checkArgument(segmentCount > 0, () -> String.format("cache segmentCount invalid: %d", segmentCount));
        this.segmentCount = segmentCount;
        return this;
    }

    public Integer getSegmentCount() {
        return segmentCount;
    }

    /**
     * Sets cache initial size of each segment's hash table.
     * default 8192.
     *
     * @param hashTableSize cache initial size of each segment's hash table
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> hashTableSize(int hashTableSize) {
        checkArgument(hashTableSize > 0, () -> String.format("cache hashTableSize invalid: %d", hashTableSize));
        this.hashTableSize = hashTableSize;
        return this;
    }

    public Integer getHashTableSize() {
        return hashTableSize;
    }

    /**
     * Sets cache capacity of the cache in bytes.
     * default 16 MB * number of CPUs ({@code java.lang.Runtime.availableProcessors()}), minimum 64 MB.
     *
     * @param capacity cache capacity of the cache in bytes
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> capacity(long capacity) {
        checkArgument(capacity > 0, () -> String.format("cache capacity invalid: %d", capacity));
        this.capacity = capacity;
        return this;
    }

    public Long getCapacity() {
        return capacity;
    }

    /**
     * Sets cache chunk size.
     * If set and positive, the <i>chunked</i> implementation will be used and each segment
     * will be divided into this amount of chunks.
     * default {@code 0}. Then <i>linked</i> implementation will be used.
     *
     * @param chunkSize cache chunk size.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> chunkSize(int chunkSize) {
        checkArgument(chunkSize > 0, () -> String.format("cache chunkSize invalid: %d", chunkSize));
        this.chunkSize = chunkSize;
        return this;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    /**
     * Sets cache keys serializer implementation. Must be configured.
     * default {@code ProtostuffCacheSerializer}
     *
     * @param keySerializer cache keys serializer implementation.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> keySerializer(CacheSerializer<K> keySerializer) {
        checkNotNull(keySerializer, () -> "cache keySerializer can't be null");
        this.keySerializer = keySerializer;
        return this;
    }

    public CacheSerializer<K> getKeySerializer() {
        return keySerializer;
    }

    /**
     * Sets cache values serializer implementation. Must be configured.
     * default {@code ProtostuffCacheSerializer}
     *
     * @param valueSerializer cache values serializer implementation.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> valueSerializer(CacheSerializer<V> valueSerializer) {
        checkNotNull(valueSerializer, () -> "cache valueSerializer can't be null");
        this.valueSerializer = valueSerializer;
        return this;
    }

    public CacheSerializer<V> getValueSerializer() {
        return valueSerializer;
    }

    /**
     * Sets cache hash table load factor which determines when rehashing occurs.
     * default {@code .75f}.
     *
     * @param loadFactor cache hash table load factor.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> loadFactor(Float loadFactor) {
        checkArgument(loadFactor > 0, () -> String.format("cache loadFactor invalid: %d", loadFactor));
        this.loadFactor = loadFactor;
        return this;
    }

    public Float getLoadFactor() {
        return loadFactor;
    }

    /**
     * If set and positive, the <i>chunked</i> implementation with fixed sized entries will be used.
     * The parameter {@code chunkSize} must be set for fixed-sized entries.
     * default {@code 0}. Then <i>linked</i> implementation will be used,
     * if {@code chunkSize} is also {@code 0}
     *
     * @param fixedKeySize cache key fixed sized.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> fixedKeySize(Integer fixedKeySize) {
        checkArgument(fixedKeySize > 0, () -> String.format("cache fixedKeySize invalid: %d", fixedKeySize));
        this.fixedKeySize = fixedKeySize;
        return this;
    }

    public Integer getFixedKeySize() {
        return fixedKeySize;
    }

    /**
     * If set and positive, the <i>chunked</i> implementation with fixed sized entries will be used.
     * The parameter {@code chunkSize} must be set for fixed-sized entries.
     * default {@code 0}. Then <i>linked</i> implementation will be used,
     * if {@code chunkSize} is also {@code 0}
     *
     * @param fixedValueSize cache value fixed sized.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> fixedValueSize(Integer fixedValueSize) {
        checkArgument(fixedValueSize > 0, () -> String.format("cache fixedValueSize invalid: %d", fixedValueSize));
        this.fixedValueSize = fixedValueSize;
        return this;
    }

    public Integer getFixedValueSize() {
        return fixedValueSize;
    }

    /**
     * Sets cache maximum size of a hash entry (including header, serialized key + serialized value)
     * defaults to capacity divided by number of segments.
     *
     * @param maxEntrySize cache maximum size (including header, serialized key + serialized value).
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> maxEntrySize(Long maxEntrySize) {
        checkArgument(maxEntrySize > 0, () -> String.format("cache maxEntrySize invalid: %d", maxEntrySize));
        this.maxEntrySize = maxEntrySize;
        return this;
    }

    public Long getMaxEntrySize() {
        return maxEntrySize;
    }

    /**
     * Sets cache whether throw {@code OutOfMemoryError} if off-heap allocation fails.
     * default {@code false}.
     *
     * @param throwOOME cache throw {@code OutOfMemoryError}.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> throwOOME(Boolean throwOOME) {
        checkNotNull(throwOOME, () -> "cache throwOOME can't be null");
        this.throwOOME = throwOOME;
        return this;
    }

    public Boolean getThrowOOME() {
        return throwOOME;
    }

    /**
     * Sets cache internal hash algorithm.
     * Valid options are: {@code XX} for xx-hash, {@code MURMUR3} or {@code CRC32}
     * Note: this setting does may only help to improve throughput in rare situations - i.e. if the key is
     * very long and you've proven that it really improves performace
     * default {@code MURMUR3}.
     *
     * @param hashAlgorighm cache internal hash algorithm.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> hashAlgorighm(HashAlgorithm hashAlgorighm) {
        checkNotNull(hashAlgorighm, () -> "cache hashAlgorighm can't be null");
        this.hashAlgorighm = hashAlgorighm;
        return this;
    }

    public HashAlgorithm getHashAlgorighm() {
        return hashAlgorighm;
    }

    /**
     * Sets cache internal unlocked.
     * If set to {@code true}, implementations will not perform any locking. The calling code has to take
     * care of synchronized access. In order to create an instance for a thread-per-core implementation,
     * set {@code segmentCount=1}, too.
     * default {@code false}.
     *
     * @param unlocked cache internal unlocked.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> unlocked(Boolean unlocked) {
        checkNotNull(unlocked, () -> "cache unlocked can't be null");
        this.unlocked = unlocked;
        return this;
    }

    public Boolean getUnlocked() {
        return unlocked;
    }

    /**
     * Sets cache TTL in milliseconds.
     * If set to a value {@code > 0}, implementations supporting TTLs will tag all entries with
     * the given TTL in <b>milliseconds</b>.
     * default {@code 60000}.
     *
     * @param defaultTTLmillis cache TTL in milliseconds.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> defaultTTLmillis(Long defaultTTLmillis) {
        checkArgument(defaultTTLmillis > 0, () -> String.format("cache defaultTTLmillis invalid: %d", defaultTTLmillis));
        this.defaultTTLmillis = defaultTTLmillis;
        return this;
    }

    public Long getDefaultTTLmillis() {
        return defaultTTLmillis;
    }

    /**
     * Sets cache timeouts.
     * default {@code true}.
     *
     * @param timeouts cache TTL in milliseconds.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> timeouts(Boolean timeouts) {
        checkNotNull(timeouts, () -> "cache timeouts can't be null");
        this.timeouts = timeouts;
        return this;
    }

    public Boolean getTimeouts() {
        return timeouts;
    }

    /**
     * Sets cache number of timeouts slots for each segment - compare with hashed wheel timer.
     * default {@code 64}.
     *
     * @param timeoutsSlots number of timeouts slots for each segment.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> timeoutsSlots(Integer timeoutsSlots) {
        checkArgument(timeoutsSlots > 0, () -> String.format("cache timeoutsSlots invalid: %d", timeoutsSlots));
        this.timeoutsSlots = timeoutsSlots;
        return this;
    }

    public Integer getTimeoutsSlots() {
        return timeoutsSlots;
    }

    /**
     * Sets cache amount of time in milliseconds for each timeouts-slot - compare with hashed wheel timer.
     * default {@code 128}.
     *
     * @param timeoutsPrecision amount of time in milliseconds for each timeouts-slot.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> timeoutsPrecision(Integer timeoutsPrecision) {
        checkArgument(timeoutsPrecision > 0, () -> String.format("cache timeoutsPrecision invalid: %d", timeoutsPrecision));
        this.timeoutsPrecision = timeoutsPrecision;
        return this;
    }

    public Integer getTimeoutsPrecision() {
        return timeoutsPrecision;
    }

    /**
     * Indirection for current time - used for unit tests.
     * default {@code default}. Default ticker using {@code System.nanoTime()} and {@code System.currentTimeMillis()}
     *
     * @param ticker current time ticker.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> ticker(Ticker ticker) {
        checkNotNull(ticker, () -> "cache ticker can't be null");
        this.ticker = ticker;
        return this;
    }

    public Ticker getTicker() {
        return ticker;
    }

    /**
     * Choose the eviction algorithm to use. Available are:
     * <ul>
     *     <li>{@link Eviction#LRU LRU}: Plain LRU - least used entry is subject to eviction</li>
     *     <li>{@link Eviction#W_TINY_LFU W-WinyLFU}: Enable use of Window Tiny-LFU. The size of the
     *     frequency sketch ("admission filter") is set to the value of {@code hashTableSize}.
     *     See <a href="http://highscalability.com/blog/2016/1/25/design-of-a-modern-cache.html">this article</a>
     *     for a description.</li>
     *     <li>{@link Eviction#NONE None}: No entries will be evicted - this effectively provides a
     *     capacity-bounded off-heap map.</li>
     * </ul>
     * default {@code W-WinyLFU}.
     *
     * @param eviction eviction algorithm.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> ticker(Eviction eviction) {
        checkNotNull(eviction, () -> "cache eviction can't be null");
        this.eviction = eviction;
        return this;
    }

    public Eviction getEviction() {
        return eviction;
    }

    /**
     * Size of the frequency sketch used by {@link Eviction#W_TINY_LFU W-WinyLFU}.
     * default {@code hashTableSize}.
     *
     * @param frequencySketchSize used by {@link Eviction#W_TINY_LFU W-WinyLFU}.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> frequencySketchSize(Integer frequencySketchSize) {
        checkArgument(frequencySketchSize > 0, () -> String.format("cache frequencySketchSize invalid: %d", frequencySketchSize));
        this.frequencySketchSize = frequencySketchSize;
        return this;
    }

    public Integer getFrequencySketchSize() {
        return frequencySketchSize;
    }

    /**
     * Size of the eden generation used by {@link Eviction#W_TINY_LFU W-WinyLFU} relative to a segment's size
     * default {@code 0.2}.
     *
     * @param edenSize used by {@link Eviction#W_TINY_LFU W-WinyLFU}.
     * @return OhcCacheOptions instance
     */
    public OhcCacheOptions<K, V> edenSize(Double edenSize) {
        checkArgument(edenSize > 0,() ->  String.format("cache edenSize invalid: %d", edenSize));
        this.edenSize = edenSize;
        return this;
    }

    public Double getEdenSize() {
        return edenSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OhcCacheOptions<?, ?> that = (OhcCacheOptions<?, ?>) o;
        return Objects.equals(segmentCount, that.segmentCount) &&
                Objects.equals(hashTableSize, that.hashTableSize) &&
                Objects.equals(capacity, that.capacity) &&
                Objects.equals(chunkSize, that.chunkSize) &&
                Objects.equals(keySerializer, that.keySerializer) &&
                Objects.equals(valueSerializer, that.valueSerializer) &&
                Objects.equals(loadFactor, that.loadFactor) &&
                Objects.equals(fixedKeySize, that.fixedKeySize) &&
                Objects.equals(fixedValueSize, that.fixedValueSize) &&
                Objects.equals(maxEntrySize, that.maxEntrySize) &&
                Objects.equals(throwOOME, that.throwOOME) &&
                hashAlgorighm == that.hashAlgorighm &&
                Objects.equals(unlocked, that.unlocked) &&
                Objects.equals(defaultTTLmillis, that.defaultTTLmillis) &&
                Objects.equals(timeouts, that.timeouts) &&
                Objects.equals(timeoutsSlots, that.timeoutsSlots) &&
                Objects.equals(timeoutsPrecision, that.timeoutsPrecision) &&
                Objects.equals(ticker, that.ticker) &&
                eviction == that.eviction &&
                Objects.equals(frequencySketchSize, that.frequencySketchSize) &&
                Objects.equals(edenSize, that.edenSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), segmentCount, hashTableSize, capacity, chunkSize, keySerializer, valueSerializer, loadFactor, fixedKeySize, fixedValueSize, maxEntrySize, throwOOME, hashAlgorighm, unlocked, defaultTTLmillis, timeouts, timeoutsSlots, timeoutsPrecision, ticker, eviction, frequencySketchSize, edenSize);
    }
}
