package cn.sliew.milky.registry;

import java.util.Optional;

/**
 * registry to be used by registries for common functionality.
 *
 * @param <E> Registered Entry
 * @param <C> Registered Entry Config
 */
public interface Registry<E, C> {

    /**
     * Find a named entry in the Registry
     *
     * @param name the  name
     */
    Optional<E> find(String name);

    /**
     * Remove an entry from the Registry
     *
     * @param name the  name
     */
    Optional<E> remove(String name);

    /**
     * Replace an existing entry in the Registry by a new one.
     *
     * @param name     the existing name
     * @param newEntry a new entry
     */
    Optional<E> replace(String name, E newEntry);

    /**
     * Get a configuration by name
     *
     * @param configName the configuration name
     * @return the found configuration if any
     */
    Optional<C> getConfiguration(String configName);

    /**
     * Adds a configuration to the registry.
     *
     * @param configName    the configuration name
     * @param configuration the added configuration
     */
    void addConfiguration(String configName, C configuration);

    /**
     * Get the default configuration
     *
     * @return the default configuration
     */
    C getDefaultConfig();
}
