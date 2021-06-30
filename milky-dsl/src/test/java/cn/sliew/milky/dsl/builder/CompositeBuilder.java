package cn.sliew.milky.dsl.builder;

import cn.sliew.milky.dsl.*;
import cn.sliew.milky.dsl.configurer.BarConfigurer;
import cn.sliew.milky.dsl.configurer.FooConfigurer;
import cn.sliew.milky.dsl.configurer.SubBarConfigurer;

public class CompositeBuilder extends AbstractConfiguredBuilder<Composite, CompositeBuilder>
        implements Builder<Composite> {

    private String foo;
    private String bar;
    private String subBar;

    @Override
    protected Composite performBuild() throws Exception {
        final Composite composite = new Composite();
        composite.setFoo(foo);
        composite.setBar(bar);
        composite.setSubBar(subBar);
        return composite;
    }

    public FooConfigurer foo() throws Exception {
        return getOrApply(new FooConfigurer());
    }

    public CompositeBuilder foo(Customizer<FooConfigurer> fooConfigurerCustomizer) throws Exception {
        fooConfigurerCustomizer.customize(getOrApply(new FooConfigurer()));
        return CompositeBuilder.this;
    }

    public BarConfigurer bar() throws Exception {
        return getOrApply(new BarConfigurer());
    }

    public CompositeBuilder bar(Customizer<BarConfigurer> barConfigurerCustomizer) throws Exception {
        barConfigurerCustomizer.customize(getOrApply(new BarConfigurer()));
        return CompositeBuilder.this;
    }

    public SubBarConfigurer subBar() throws Exception {
        return getOrApply(new SubBarConfigurer());
    }

    public CompositeBuilder subBar(Customizer<SubBarConfigurer> subBarConfigurerCustomizer) throws Exception {
        subBarConfigurerCustomizer.customize(getOrApply(new SubBarConfigurer()));
        return CompositeBuilder.this;
    }

    private <C extends AbstractConfigurer<Composite, CompositeBuilder>> C getOrApply(C configurer) throws Exception {
        C existingConfig = (C) getConfigurer(configurer.getClass());
        if (existingConfig != null) {
            return existingConfig;
        }
        return apply(configurer);
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public void setSubBar(String subBar) {
        this.subBar = subBar;
    }
}
