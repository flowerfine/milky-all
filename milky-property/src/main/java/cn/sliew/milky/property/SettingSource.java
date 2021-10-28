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

    /**
     * Return whether this {@code PropertySource} contains the given name.
     * <p>This implementation simply checks for a {@code null} return value
     * from {@link #getProperty(String)}. Subclasses may wish to implement
     * a more efficient algorithm if possible.
     * @param name the property name to find
     */
    public boolean containsProperty(String name) {
        return (getProperty(name) != null);
    }

    /**
     * Return the value associated with the given name,
     * or {@code null} if not found.
     * @param name the property to find
     * @see PropertyResolver#getRequiredProperty(String)
     */
    public abstract Object getProperty(String name);
}
