package cn.sliew.milky.common.settings;

import java.util.*;
import java.util.function.Predicate;

final class FilteredMap extends AbstractMap<String, Object> {

    private final Map<String, Object> delegate;
    private final Predicate<String> filter;
    private final String prefix;
    // we cache that size since we have to iterate the entire set
    // this is safe to do since this map is only used with unmodifiable maps
    private int size = -1;

    FilteredMap(Map<String, Object> delegate, Predicate<String> filter, String prefix) {
        this.delegate = delegate;
        this.filter = filter;
        this.prefix = prefix;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> delegateSet = delegate.entrySet();
        AbstractSet<Entry<String, Object>> filterSet = new AbstractSet<Entry<String, Object>>() {

            @Override
            public Iterator<Entry<String, Object>> iterator() {
                Iterator<Entry<String, Object>> iter = delegateSet.iterator();

                return new Iterator<Entry<String, Object>>() {
                    private int numIterated;
                    private Entry<String, Object> currentElement;

                    @Override
                    public boolean hasNext() {
                        if (currentElement != null) {
                            return true; // protect against calling hasNext twice
                        } else {
                            if (numIterated == size) { // early terminate
                                assert size != -1 : "size was never set: " + numIterated + " vs. " + size;
                                return false;
                            }
                            while (iter.hasNext()) {
                                if (filter.test((currentElement = iter.next()).getKey())) {
                                    numIterated++;
                                    return true;
                                }
                            }
                            // we didn't find anything
                            currentElement = null;
                            return false;
                        }
                    }

                    @Override
                    public Entry<String, Object> next() {
                        if (currentElement == null && hasNext() == false) { // protect against no #hasNext call or not respecting it

                            throw new NoSuchElementException("make sure to call hasNext first");
                        }
                        final Entry<String, Object> current = this.currentElement;
                        this.currentElement = null;
                        if (prefix == null) {
                            return current;
                        }
                        return new Entry<String, Object>() {
                            @Override
                            public String getKey() {
                                return current.getKey().substring(prefix.length());
                            }

                            @Override
                            public Object getValue() {
                                return current.getValue();
                            }

                            @Override
                            public Object setValue(Object value) {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                };
            }

            @Override
            public int size() {
                return FilteredMap.this.size();
            }
        };
        return filterSet;
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            final String theKey = prefix == null ? (String) key : prefix + key;
            if (filter.test(theKey)) {
                return delegate.get(theKey);
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            final String theKey = prefix == null ? (String) key : prefix + key;
            if (filter.test(theKey)) {
                return delegate.containsKey(theKey);
            }
        }
        return false;
    }

    @Override
    public int size() {
        if (size == -1) {
            size = Math.toIntExact(delegate.keySet().stream().filter(filter).count());
        }
        return size;
    }
}