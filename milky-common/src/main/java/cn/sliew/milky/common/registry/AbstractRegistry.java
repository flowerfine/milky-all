package cn.sliew.milky.common.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * Abstract registry to be shared with all resilience4j registries
 */
public abstract class AbstractRegistry<E, C> implements Registry<E, C> {

    protected static final String DEFAULT_CONFIG = "default";

    protected final RegistryStore<E> entryMap;
    protected final ConcurrentMap<String, C> configurations;

    public AbstractRegistry(C defaultConfig) {
        this.configurations = new ConcurrentHashMap<>();
        this.entryMap = new InMemoryRegistryStore<E>();
        this.configurations
                .put(DEFAULT_CONFIG, Objects.requireNonNull(defaultConfig, "Config must not be null"));
    }

    protected E computeIfAbsent(String name, Supplier<E> supplier) {
        return entryMap.computeIfAbsent(Objects.requireNonNull(name, "Name must not be null"), k -> supplier.get());
    }

    @Override
    public Optional<E> find(String name) {
        return entryMap.find(name);
    }

    @Override
    public Optional<E> remove(String name) {
        return entryMap.remove(name);
    }

    @Override
    public Optional<E> replace(String name, E newEntry) {
        return entryMap.replace(name, newEntry);
    }

    @Override
    public void addConfiguration(String configName, C configuration) {
        if (configName.equals(DEFAULT_CONFIG)) {
            throw new IllegalArgumentException(
                    "You cannot use 'default' as a configuration name as it is preserved for default configuration");
        }
        this.configurations.put(configName, configuration);
    }

    @Override
    public Optional<C> getConfiguration(String configName) {
        return Optional.ofNullable(this.configurations.get(configName));
    }

    @Override
    public C getDefaultConfig() {
        return configurations.get(DEFAULT_CONFIG);
    }
}
