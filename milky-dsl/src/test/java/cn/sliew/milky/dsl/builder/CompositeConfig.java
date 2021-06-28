package cn.sliew.milky.dsl.builder;


import cn.sliew.milky.dsl.*;
import cn.sliew.milky.dsl.configurer.BarConfigurer;
import cn.sliew.milky.dsl.configurer.FooConfigurer;
import cn.sliew.milky.dsl.configurer.SubBarConfigurer;

public class CompositeConfig extends AbstractConfiguredBuilder<Composite, CompositeConfig>
        implements Builder<Composite> {

    @Override
    protected Composite performBuild() throws Exception {
        return null;
    }

    public FooConfigurer<CompositeConfig> foo() throws Exception {
        return getOrApply(new FooConfigurer<>());
    }

    public CompositeConfig foo(Customizer<FooConfigurer<CompositeConfig>> fooConfigurerCustomizer) throws Exception {
        fooConfigurerCustomizer.customize(getOrApply(new FooConfigurer<>()));
        return CompositeConfig.this;
    }

    public BarConfigurer<CompositeConfig> bar() throws Exception {
        return getOrApply(new BarConfigurer<>());
    }

    public CompositeConfig bar(Customizer<BarConfigurer<CompositeConfig>> barConfigurerCustomizer) throws Exception {
        barConfigurerCustomizer.customize(getOrApply(new BarConfigurer<>()));
        return CompositeConfig.this;
    }

    public SubBarConfigurer<CompositeConfig> subBar() throws Exception {
        return getOrApply(new SubBarConfigurer<>());
    }

    public CompositeConfig subBar(Customizer<SubBarConfigurer<CompositeConfig>> subBarConfigurerCustomizer) throws Exception {
        subBarConfigurerCustomizer.customize(getOrApply(new SubBarConfigurer<>()));
        return CompositeConfig.this;
    }

    private <C extends AbstractConfigurer<Composite, CompositeConfig>> C getOrApply(C configurer) throws Exception {
        C existingConfig = (C) getConfigurer(configurer.getClass());
        if (existingConfig != null) {
            return existingConfig;
        }
        return apply(configurer);
    }
}
