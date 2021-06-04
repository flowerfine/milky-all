package cn.sliew.milky.common.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents differences between two maps of objects and is used as base class for different map implementations.
 * <p>
 * Implements serialization. How differences are applied is left to subclasses.
 *
 * @param <K> the type of map keys
 * @param <T> the type of map values
 * @param <M> the map implementation type
 */
public abstract class MapDiff<K, T, M> implements Diff<M> {

    protected final List<K> deletes;
    protected final Map<K, Diff<T>> diffs; // incremental updates
    protected final Map<K, T> upserts; // additions or full updates

    public MapDiff() {
        deletes = new ArrayList<>();
        diffs = new HashMap<>();
        upserts = new HashMap<>();
    }

    public MapDiff(List<K> deletes, Map<K, Diff<T>> diffs, Map<K, T> upserts) {
        this.deletes = deletes;
        this.diffs = diffs;
        this.upserts = upserts;
    }

    /**
     * The keys that, when this diff is applied to a map, should be removed from the map.
     *
     * @return the list of keys that are deleted
     */
    public List<K> getDeletes() {
        return deletes;
    }

    /**
     * Map entries that, when this diff is applied to a map, should be
     * incrementally updated. The incremental update is represented using
     * the {@link Diff} interface.
     *
     * @return the map entries that are incrementally updated
     */
    public Map<K, Diff<T>> getDiffs() {
        return diffs;
    }

    /**
     * Map entries that, when this diff is applied to a map, should be
     * added to the map or fully replace the previous value.
     *
     * @return the map entries that are additions or full updates
     */
    public Map<K, T> getUpserts() {
        return upserts;
    }
}