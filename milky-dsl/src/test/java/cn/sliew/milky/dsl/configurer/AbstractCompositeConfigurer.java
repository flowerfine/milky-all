package cn.sliew.milky.dsl.configurer;

import cn.sliew.milky.dsl.AbstractConfigurer;
import cn.sliew.milky.dsl.Composite;
import cn.sliew.milky.dsl.ConfigurableBuilder;

public abstract class AbstractCompositeConfigurer<T extends AbstractCompositeConfigurer<T, B>, B extends ConfigurableBuilder<Composite, B>>
        extends AbstractConfigurer<Composite, B> {

}
