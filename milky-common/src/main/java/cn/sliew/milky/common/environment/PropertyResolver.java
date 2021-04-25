package cn.sliew.milky.common.environment;

import java.util.Optional;
import java.util.function.Function;

/**
 * Interface for resolving properties against any underlying source.
 */
public interface PropertyResolver {

    /**
     * Whether the given property key is available.
     *
     * @return true if the given property key is available for resolution
     * otherwise false.
     */
    boolean containsProperty(String key);

    /**
     * Find the property value associated with the given key.
     * {@code Optional.empty()} will be returned if the key cannot be resolved.
     *
     * @param key the property name to resolve
     * @return property value
     * @see #getProperty(String, String)
     * @see #getProperty(String, Function)
     * @see #getRequiredProperty(String)
     */
    Optional<String> getProperty(String key);

    /**
     * Find the property value associated with the given key.
     * If the associated property value not found, defaultValue will be returned.
     *
     * @param key          the property name to resolve
     * @param defaultValue the default value to return if no value is found
     * @return property value
     * @see #getRequiredProperty(String)
     * @see #getProperty(String, Function)
     */
    String getProperty(String key, String defaultValue);

    /**
     * Find the property value associated with the given key.
     * {@code Optional.empty()} will be returned if the key cannot be resolved.
     *
     * @param key             the property name to resolve
     * @param mappingFunction the mapping function convert original object to target objet.
     * @return property value
     * @see #getRequiredProperty(String, Function)
     */
    <R, T> Optional<T> getProperty(String key, Function<R, T> mappingFunction);

    /**
     * Find the property value associated with the given key.
     * If the associated property value not found, defaultValue will be returned.
     *
     * @param key             the property name to resolve
     * @param mappingFunction the mapping function convert original object to target objet
     * @param defaultValue    the default value to return if no value is found
     * @return property value
     * @see #getRequiredProperty(String, Function)
     */
    <R, T> T getProperty(String key, Function<R, T> mappingFunction, T defaultValue);

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
     * @param mappingFunction the mapping function convert original object to target objet
     * @return property value
     * @throws IllegalStateException if the given key cannot be resolved
     */
    <R, T> T getRequiredProperty(String key, Function<R, T> mappingFunction) throws IllegalStateException;

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
     * no default value are ignored and passed through unchanged.
     *
     * @param text the String to resolve
     * @return the resolved String (never {@code null})
     * @throws IllegalArgumentException if given text param is {@code null}
     * @see #resolveRequiredPlaceholders
     */
    String resolvePlaceholders(String text);

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
     * no default value will cause an IllegalArgumentException to be thrown.
     *
     * @return the resolved String (never {@code null})
     * @throws IllegalArgumentException if given text is {@code null}
     *                                  or if any placeholders are unresolvable
     */
    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

    /**
     * Set the prefix that placeholders replaced by this resolver must begin with.
     */
    void setPlaceholderPrefix(String placeholderPrefix);

    /**
     * Set the suffix that placeholders replaced by this resolver must end with.
     */
    void setPlaceholderSuffix(String placeholderSuffix);

    /**
     * Specify the separating character between the placeholders replaced by this
     * resolver and their associated default value, or {@code null} if no such
     * special character should be processed as a value separator.
     */
    void setValueSeparator(String valueSeparator);

}
