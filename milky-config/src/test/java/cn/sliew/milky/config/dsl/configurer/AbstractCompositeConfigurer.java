package cn.sliew.milky.config.dsl.configurer;

import cn.sliew.milky.config.dsl.AbstractConfigurer;
import cn.sliew.milky.config.dsl.Composite;
import cn.sliew.milky.config.dsl.ConfigurableBuilder;

public abstract class AbstractCompositeConfigurer<T extends AbstractCompositeConfigurer<T, B>, B extends ConfigurableBuilder<Composite, B>>
        extends AbstractConfigurer<Composite, B> {

}
