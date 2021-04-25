package cn.sliew.milky.common.environment;

import java.util.Optional;

public interface Environment {

    /**
     * Return whether this {@code PropertySource} contains the given name.
     * <p>This implementation simply checks for a {@code null} return value
     * from {@link #getProperty(String)}. Subclasses may wish to implement
     * a more efficient algorithm if possible.
     * @param name the property name to find
     */
    default boolean containsProperty(String name) {
        return (getProperty(name) != null);
    }

    /**
     * Return the value associated with the given name, or {@code null} if not found.
     * @param name the property to find
     */
    Optional<Object> getProperty(String name);
}
