package cn.sliew.milky.property;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@FunctionalInterface
public interface Validator<T> {

    /**
     * Validate this setting's value in isolation.
     *
     * @param value the value of this setting
     */
    void validate(T value);

    /**
     * Validate this setting against its dependencies, specified by {@link #settings()}.
     * The default implementation does nothing, accepting any value as valid as long as
     * it passes the validation in {@link #validate(Object)}.
     *
     * @param value    the value of this setting
     * @param settings a map from the settings specified by {@link #settings()}} to their values
     */
    default void validate(T value, Map<Setting<T>, T> settings) {
    }


    /**
     * The settings on which the validity of this setting depends.
     * The values of the specified settings are passed to {@link #validate(Object, Map)}.
     * By default this returns an empty iterator, indicating that this setting does not
     * depend on any other settings.
     *
     * @return the settings on which the validity of this setting depends.
     */
    default Iterator<Setting<T>> settings() {
        return Collections.emptyIterator();
    }
}
