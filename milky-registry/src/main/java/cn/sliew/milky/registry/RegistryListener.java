package cn.sliew.milky.registry;

/**
 * An {@link RegistryEvent} listener can be used to be aware of registry event.
 */
public interface RegistryListener {

    void onAdded();

    void onRemoved();

    void onReplaced();
}
