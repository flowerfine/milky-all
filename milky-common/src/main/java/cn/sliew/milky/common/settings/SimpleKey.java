package cn.sliew.milky.common.settings;

import java.util.Objects;

public class SimpleKey implements Key {

    protected final String key;

    public SimpleKey(String key) {
        this.key = key;
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
