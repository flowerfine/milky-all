package cn.sliew.milky.config.dsl.configurer;

import cn.sliew.milky.config.dsl.AbstractConfigurer;
import cn.sliew.milky.config.dsl.Composite;
import cn.sliew.milky.config.dsl.builder.CompositeBuilder;

public abstract class AbstractCompositeConfigurer<T extends AbstractCompositeConfigurer<T, B>, B extends CompositeBuilder<B>>
        extends AbstractConfigurer<Composite, B> {

}
