package cn.sliew.milky.config.dsl.configurer;

import cn.sliew.milky.config.dsl.builder.CompositeBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class SubBarConfigurer <H extends CompositeBuilder<H>> extends AbstractCompositeConfigurer<SubBarConfigurer<H>, H> {

    private String subBar;

    public SubBarConfigurer<H> subBar(String subBar) {
        notBlank(subBar, () -> "subBar cannot be blank");

        this.subBar = subBar;
        return this;
    }

    @Override
    public void configure(H composite) throws Exception {
        System.out.println("FooConfigurer configure composite with " + subBar);
    }
}
