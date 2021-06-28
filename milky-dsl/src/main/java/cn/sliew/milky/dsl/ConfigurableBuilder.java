package cn.sliew.milky.dsl;

public interface ConfigurableBuilder<O, H extends ConfigurableBuilder<O, H>> extends Builder<O> {

    /**
     * Gets the {@link Configurer} by its class name or <code>null</code> if not
     * found. Note that object hierarchies are not considered.
     *
     * @param clazz the Class of the {@link Configurer} to attempt to get.
     */
    <C extends Configurer<O, H>> C getConfigurer(Class<C> clazz);

    /**
     * Removes the {@link Configurer} by its class name or <code>null</code> if
     * not found. Note that object hierarchies are not considered.
     *
     * @param clazz the Class of the {@link Configurer} to attempt to remove.
     * @return the {@link Configurer} that was removed or null if not found
     */
    <C extends Configurer<O, H>> C removeConfigurer(Class<C> clazz);
}
