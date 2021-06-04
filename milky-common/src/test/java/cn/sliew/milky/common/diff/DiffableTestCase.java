package cn.sliew.milky.common.diff;

import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import cn.sliew.milky.test.MilkyTestCase;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class DiffableTestCase extends MilkyTestCase {

    private static final Logger logger = LoggerFactory.getLogger(DiffableTestCase.class);

    @Test
    void testJKDMapDiff() throws IOException {
        new JdkMapDriver<TestDiffable>() {
            @Override
            protected boolean diffableValues() {
                return true;
            }

            @Override
            protected TestDiffable createValue(Integer key, boolean before) {
                return new TestDiffable(String.valueOf(before ? key : key + 1));
            }

            @Override
            protected MapDiff diff(Map<Integer, TestDiffable> before, Map<Integer, TestDiffable> after) {
                return JdkMapDiff.diff(before, after);
            }
        }.execute();

        new JdkMapDriver<String>() {
            @Override
            protected boolean diffableValues() {
                return false;
            }

            @Override
            protected String createValue(Integer key, boolean before) {
                return String.valueOf(before ? key : key + 1);
            }

            @Override
            protected MapDiff diff(Map<Integer, String> before, Map<Integer, String> after) {
                return JdkMapDiff.diff(before, after);
            }
        }.execute();
    }

    /**
     * Class that abstracts over specific map implementation type and value kind (Diffable or not)
     *
     * @param <T> map type
     * @param <V> value type
     */
    public abstract class MapDriver<T, V> {
        protected final Set<Integer> keys = randomPositiveIntSet();
        protected final Set<Integer> keysToRemove = new HashSet<>(randomSubsetOf(randomIntBetween(0, keys.size()), keys.toArray(new Integer[0])));
        protected final Set<Integer> keysThatAreNotRemoved = Sets.difference(keys, keysToRemove);
        protected final Set<Integer> keysToOverride = new HashSet<>(randomSubsetOf(randomIntBetween(0, keysThatAreNotRemoved.size()),
                keysThatAreNotRemoved.toArray(new Integer[keysThatAreNotRemoved.size()])));
        // make sure keysToAdd does not contain elements in keys
        protected final Set<Integer> keysToAdd = Sets.difference(randomPositiveIntSet(), keys);
        protected final Set<Integer> keysUnchanged = Sets.difference(keysThatAreNotRemoved, keysToOverride);

        protected final boolean useProtoForDiffableSerialization = randomBoolean();

        private Set<Integer> randomPositiveIntSet() {
            int maxSetSize = randomIntBetween(0, 6);
            Set<Integer> result = new HashSet<>();
            for (int i = 0; i < maxSetSize; i++) {
                // due to duplicates, set size can be smaller than maxSetSize
                result.add(randomIntBetween(0, 100));
            }
            return result;
        }

        /**
         * whether we operate on {@link Diffable} values
         */
        protected abstract boolean diffableValues();

        /**
         * functions that determines value in "before" or "after" map based on key
         */
        protected abstract V createValue(Integer key, boolean before);

        /**
         * creates map based on JDK-based map
         */
        protected abstract T createMap(Map<Integer, V> values);

        /**
         * calculates diff between two maps
         */
        protected abstract MapDiff<Integer, V, T> diff(T before, T after);

        /**
         * gets element at key "key" in map "map"
         */
        protected abstract V get(T map, Integer key);

        /**
         * returns size of given map
         */
        protected abstract int size(T map);

        /**
         * executes the actual test
         */
        public void execute() throws IOException {
            logger.debug("Keys in 'before' map: {}", keys);
            logger.debug("Keys to remove: {}", keysToRemove);
            logger.debug("Keys to override: {}", keysToOverride);
            logger.debug("Keys to add: {}", keysToAdd);

            logger.debug("--> creating 'before' map");
            Map<Integer, V> before = new HashMap<>();
            for (Integer key : keys) {
                before.put(key, createValue(key, true));
            }
            T beforeMap = createMap(before);

            logger.debug("--> creating 'after' map");
            Map<Integer, V> after = new HashMap<>();
            after.putAll(before);
            for (Integer key : keysToRemove) {
                after.remove(key);
            }
            for (Integer key : keysToOverride) {
                after.put(key, createValue(key, false));
            }
            for (Integer key : keysToAdd) {
                after.put(key, createValue(key, false));
            }
            T afterMap = createMap(unmodifiableMap(after));

            MapDiff<Integer, V, T> diffMap = diff(beforeMap, afterMap);

            // check properties of diffMap
            assertThat(new HashSet(diffMap.getDeletes()), equalTo(keysToRemove));
            if (diffableValues()) {
                assertThat(diffMap.getDiffs().keySet(), equalTo(keysToOverride));
                for (Integer key : keysToOverride) {
                    assertThat(diffMap.getDiffs().get(key).apply(get(beforeMap, key)), equalTo(get(afterMap, key)));
                }
                assertThat(diffMap.getUpserts().keySet(), equalTo(keysToAdd));
                for (Integer key : keysToAdd) {
                    assertThat(diffMap.getUpserts().get(key), equalTo(get(afterMap, key)));
                }
            } else {
                assertThat(diffMap.getDiffs(), equalTo(emptyMap()));
                Set<Integer> keysToAddAndOverride = Sets.union(keysToAdd, keysToOverride);
                assertThat(diffMap.getUpserts().keySet(), equalTo(keysToAddAndOverride));
                for (Integer key : keysToAddAndOverride) {
                    assertThat(diffMap.getUpserts().get(key), equalTo(get(afterMap, key)));
                }
            }

            T appliedDiffMap = diffMap.apply(beforeMap);

            // check properties of appliedDiffMap
            assertThat(size(appliedDiffMap), equalTo(keys.size() - keysToRemove.size() + keysToAdd.size()));
            for (Integer key : keysToRemove) {
                assertThat(get(appliedDiffMap, key), nullValue());
            }
            for (Integer key : keysUnchanged) {
                assertThat(get(appliedDiffMap, key), equalTo(get(beforeMap, key)));
            }
            for (Integer key : keysToOverride) {
                assertThat(get(appliedDiffMap, key), not(equalTo(get(beforeMap, key))));
                assertThat(get(appliedDiffMap, key), equalTo(get(afterMap, key)));
            }
            for (Integer key : keysToAdd) {
                assertThat(get(appliedDiffMap, key), equalTo(get(afterMap, key)));
            }
        }
    }

    abstract class JdkMapDriver<V> extends MapDriver<Map<Integer, V>, V> {

        @Override
        protected Map<Integer, V> createMap(Map values) {
            return values;
        }

        @Override
        protected V get(Map<Integer, V> map, Integer key) {
            return map.get(key);
        }

        @Override
        protected int size(Map<Integer, V> map) {
            return map.size();
        }
    }

    public static class TestDiffable extends AbstractDiffable<TestDiffable> {

        private final String value;

        public TestDiffable(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestDiffable that = (TestDiffable) o;

            return !(value != null ? !value.equals(that.value) : that.value != null);

        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

}
