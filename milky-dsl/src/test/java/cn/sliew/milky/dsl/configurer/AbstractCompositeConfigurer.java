package cn.sliew.milky.dsl.configurer;

import cn.sliew.milky.dsl.AbstractConfigurer;
import cn.sliew.milky.dsl.Composite;
import cn.sliew.milky.dsl.ConfigurableBuilder;

public abstract class AbstractCompositeConfigurer<T extends AbstractCompositeConfigurer<T, B>, B extends ConfigurableBuilder<Composite, B>>
        extends AbstractConfigurer<Composite, B> {

    private String foo;

    private String bar;

    private String subBar;

    public String getFoo() {
        return foo;
    }

    public String getBar() {
        return bar;
    }

    public String getSubBar() {
        return subBar;
    }
}
