package cn.sliew.milky.registry;

import java.util.Optional;

/**
 * registry to be used by registries for common functionality.
 * <p>
 * fixme 根据 config 生成 Entry 的功能，以便恢复 Entry.
 *
 * @param <E> Registered Entry
 * @param <C> Registered Entry Config
 */
public interface Registry<E, C> extends Iterable<E> {

    /**
     * Find a named entry in the Registry
     *
     * @param name the name
     */
    Optional<E> find(String name);

    /**
     * Remove an entry from the Registry
     *
     * @param name the name
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
     * Return named entry whether exists in the Registry.
     *
     * @param name the name
     */
    boolean exist(String name);

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

    /**
     * Register entry event such as add, remove or replace
     *
     * @param listener the listener
     */
    void registerListener(RegistryListener<E> listener);
}
