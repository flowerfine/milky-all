package cn.sliew.milky.common.diff;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents differences between two Maps of (possibly diffable) objects.
 *
 * @param <T> the diffable object
 */
public class JdkMapDiff<K, T> extends MapDiff<K, T, Map<K, T>> {

    public static <K, T> MapDiff<K, T, Map<K, T>> diff(Map<K, T> before, Map<K, T> after) {
        assert after != null && before != null;
        return new JdkMapDiff<>(before, after);
    }

    public JdkMapDiff(Map<K, T> before, Map<K, T> after) {
        assert after != null && before != null;

        for (K key : before.keySet()) {
            if (!after.containsKey(key)) {
                deletes.add(key);
            }
        }

        for (Map.Entry<K, T> partIter : after.entrySet()) {
            T beforePart = before.get(partIter.getKey());
            T afterPart = partIter.getValue();
            if (beforePart == null) {
                upserts.put(partIter.getKey(), partIter.getValue());
            } else if (afterPart.equals(beforePart) == false) {
                if (beforePart instanceof Diffable && afterPart instanceof Diffable) {
                    Diffable beforeDiffable = (Diffable) beforePart;
                    Diffable afterDiffable = (Diffable) afterPart;
                    diffs.put(partIter.getKey(), afterDiffable.diff(beforeDiffable));
                } else {
                    upserts.put(partIter.getKey(), partIter.getValue());
                }
            }
        }
    }

    @Override
    public Map<K, T> apply(Map<K, T> map) {
        Map<K, T> builder = new HashMap<>(map);

        for (K part : deletes) {
            builder.remove(part);
        }

        for (Map.Entry<K, Diff<T>> diff : diffs.entrySet()) {
            builder.put(diff.getKey(), diff.getValue().apply(builder.get(diff.getKey())));
        }

        for (Map.Entry<K, T> upsert : upserts.entrySet()) {
            builder.put(upsert.getKey(), upsert.getValue());
        }
        return builder;
    }
}