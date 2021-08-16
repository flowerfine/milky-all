package cn.sliew.milky.registry;

/**
 * An {@link RegistryEvent} listener can be used to be aware of registry event.
 */
public interface RegistryListener<E> {

    void onAdded(EntryAddedEvent<E> event);

    void onRemoved(EntryRemovedEvent<E> event);

    void onReplaced(EntryReplacedEvent<E> event);
}
