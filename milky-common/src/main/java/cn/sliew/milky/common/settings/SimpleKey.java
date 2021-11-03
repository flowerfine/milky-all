package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.primitives.Strings;

import java.util.Objects;

public class SimpleKey implements Key {

    protected final String key;
    private final String description;

    public SimpleKey(String key) {
        this(key, Strings.empty());
    }

    public SimpleKey(String key, String description) {
        this.key = key;
        this.description = description;
    }

    @Override
    public String getDisplayName() {
        return key;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean match(String key) {
        return this.key.equals(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleKey simpleKey = (SimpleKey) o;
        return Objects.equals(key, simpleKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key;
    }

}
