package cn.sliew.milky.config.dsl.builder;

import cn.sliew.milky.config.dsl.Builder;
import cn.sliew.milky.config.dsl.Composite;
import cn.sliew.milky.config.dsl.Configurer;

public interface CompositeBuilder<H extends CompositeBuilder<H>> extends Builder<Composite> {

    /**
     * Gets the {@link Configurer} by its class name or <code>null</code> if not
     * found. Note that object hierarchies are not considered.
     * @param clazz the Class of the {@link Configurer} to attempt to get.
     */
    <C extends Configurer<Composite, H>> C getConfigurer(Class<C> clazz);

    /**
     * Removes the {@link Configurer} by its class name or <code>null</code> if
     * not found. Note that object hierarchies are not considered.
     * @param clazz the Class of the {@link Configurer} to attempt to remove.
     * @return the {@link Configurer} that was removed or null if not found
     */
    <C extends Configurer<Composite, H>> C removeConfigurer(Class<C> clazz);
}
