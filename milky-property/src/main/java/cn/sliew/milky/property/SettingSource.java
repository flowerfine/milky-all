package cn.sliew.milky.property;

import java.util.Set;

public interface SettingSource<T> extends Mergeable {

    /**
     * @return the name of this {@code SettingSource}.
     */
    String getName();

    /**
     * @return the underlying source object for this {@code SettingSource}.
     */
    T getSource();

    boolean isEmpty();
    int size();

    Set<String> getKeySet();

    default boolean contains(String name) {
        return (get(name) != null);
    }
    Object get(String name);
}
