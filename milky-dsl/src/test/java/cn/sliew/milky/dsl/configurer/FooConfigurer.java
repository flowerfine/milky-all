package cn.sliew.milky.dsl.configurer;

import cn.sliew.milky.dsl.builder.CompositeBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class FooConfigurer extends AbstractCompositeConfigurer<FooConfigurer, CompositeBuilder> {

    private String foo;

    public FooConfigurer foo(String foo) {
        notBlank(foo, () -> "foo cannot be blank");

        this.foo = foo;
        return this;
    }

    @Override
    public void configure(CompositeBuilder composite) throws Exception {
        composite.setFoo(foo);
        System.out.println("FooConfigurer configure composite with " + foo);
    }
}
