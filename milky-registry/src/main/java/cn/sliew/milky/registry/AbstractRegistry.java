package cn.sliew.milky.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

public abstract class AbstractRegistry<E, C> implements Registry<E, C> {

    protected final RegistryStore<E> entries;
    protected final ConcurrentMap<String, C> configurations;
    protected final CompositeRegistryListener<E> listeners;

    public AbstractRegistry() {
        this.entries = new InMemoryRegistryStore<>();
        this.configurations = new ConcurrentHashMap<>();
        this.listeners = new CompositeRegistryListener<>(Collections.emptyList());
    }

    protected E computeIfAbsent(String name, Supplier<E> supplier) {
        return entries.computeIfAbsent(checkNotNull(name, () -> "Name must not be null"), k -> {
            E entry = supplier.get();
            listeners.onAdded(new EntryAddedEvent<>(entry));
            return entry;
        });
    }

    @Override
    public Optional<E> find(String name) {
        return entries.find(name);
    }

    @Override
    public Optional<E> remove(String name) {
        Optional<E> removedEntry = entries.remove(name);
        removedEntry.ifPresent(entry -> listeners.onRemoved(new EntryRemovedEvent<>(entry)));
        return removedEntry;
    }

    @Override
    public Optional<E> replace(String name, E newEntry) {
        Optional<E> replacedEntry = entries.replace(name, newEntry);
        replacedEntry.ifPresent(oldEntry -> listeners.onReplaced(new EntryReplacedEvent<>(oldEntry, newEntry)));
        return replacedEntry;
    }

    @Override
    public boolean exist(String name) {
        return entries.exists(name);
    }

    @Override
    public Iterator<E> iterator() {
        return entries.values().iterator();
    }

    @Override
    public void addConfiguration(String configName, C configuration) {
        this.configurations.put(configName, configuration);
    }

    @Override
    public Optional<C> getConfiguration(String configName) {
        return Optional.ofNullable(this.configurations.get(configName));
    }

    private class CompositeRegistryListener<E> implements RegistryListener<E> {

        private final List<RegistryListener<E>> delegates;

        public CompositeRegistryListener(List<RegistryListener<E>> delegates) {
            this.delegates = new ArrayList<>(checkNotNull(delegates));
        }

        private void register(RegistryListener<E> listener) {
            if (delegates.contains(listener)) {
                throw new IllegalStateException(String.format("duplicate register for %s", listener));
            }
            delegates.add(listener);
        }

        @Override
        public void onAdded(EntryAddedEvent<E> event) {
            delegates.forEach(listener -> listener.onAdded(event));
        }

        @Override
        public void onRemoved(EntryRemovedEvent<E> event) {
            delegates.forEach(listener -> listener.onRemoved(event));
        }

        @Override
        public void onReplaced(EntryReplacedEvent<E> event) {
            delegates.forEach(listener -> listener.onReplaced(event));
        }
    }
}
