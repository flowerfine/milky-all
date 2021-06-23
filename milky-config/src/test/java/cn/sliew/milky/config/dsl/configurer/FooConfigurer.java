package cn.sliew.milky.config.dsl.configurer;

import cn.sliew.milky.config.dsl.Composite;
import cn.sliew.milky.config.dsl.ConfigurableBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class FooConfigurer<H extends ConfigurableBuilder<Composite, H>> extends AbstractCompositeConfigurer<FooConfigurer<H>, H> {

    private String foo;

    public FooConfigurer<H> foo(String foo) {
        notBlank(foo, () -> "foo cannot be blank");

        this.foo = foo;
        return this;
    }

    @Override
    public void configure(H composite) throws Exception {
        System.out.println("FooConfigurer configure composite with " + foo);
    }
}
