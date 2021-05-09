package cn.sliew.milky.config;

import java.util.Optional;
import java.util.function.Function;

public interface PropertyResolver {

    /**
     * Whether the given property key is available.
     *
     * @return true if the given property key is available for resolution
     * otherwise false.
     */
    boolean contains(String property);

    /**
     * Find the property value associated with the given key.
     * {@code Optional.empty()} will be returned if the key cannot be resolved.
     *
     * @param property the property name to resolve
     * @return property value
     * @see #getProperty(String, String)
     * @see #getProperty(String, Function)
     * @see #getRequiredProperty(String)
     */
    Optional<String> getProperty(String property);

    /**
     * Find the property value associated with the given key.
     * If the associated property value not found, defaultValue will be returned.
     *
     * @param property          the property name to resolve
     * @param defaultValue the default value to return if no value is found
     * @return property value
     * @see #getRequiredProperty(String)
     * @see #getProperty(String, Function)
     */
    String getProperty(String property, String defaultValue);

    /**
     * Find the property value associated with the given key.
     * {@code Optional.empty()} will be returned if the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param mapping the mapping function convert original object to target objet.
     * @return property value
     * @see #getRequiredProperty(String, Function)
     */
    <R, T> Optional<T> getProperty(String key, Function<R, T> mapping);

    /**
     * Find the property value associated with the given key.
     * If the associated property value not found, defaultValue will be returned.
     *
     * @param key             the property name to resolve
     * @param mapping the mapping function convert original object to target objet
     * @param defaultValue    the default value to return if no value is found
     * @return property value
     * @see #getRequiredProperty(String, Function)
     */
    <R, T> T getProperty(String key, Function<R, T> mapping, T defaultValue);

    /**
     * Find the property value associated with the given key (never {@code null}).
     *
     * @param key the property name to resolve
     * @return property value
     * @throws IllegalStateException if the key cannot be resolved
     * @see #getRequiredProperty(String, Function)
     */
    String getRequiredProperty(String key) throws IllegalStateException;

    /**
     * Find the property value associated with the given key (never {@code null}).
     *
     * @param key             the property name to resolve
     * @param mapping the mapping function convert original object to target objet
     * @return property value
     * @throws IllegalStateException if the given key cannot be resolved
     */
    <R, T> T getRequiredProperty(String key, Function<R, T> mapping) throws IllegalStateException;

}
