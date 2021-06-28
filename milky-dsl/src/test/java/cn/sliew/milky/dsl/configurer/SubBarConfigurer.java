package cn.sliew.milky.dsl.configurer;

import cn.sliew.milky.dsl.Composite;
import cn.sliew.milky.dsl.ConfigurableBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class SubBarConfigurer <H extends ConfigurableBuilder<Composite, H>> extends AbstractCompositeConfigurer<SubBarConfigurer<H>, H> {

    private String subBar;

    public SubBarConfigurer<H> subBar(String subBar) {
        notBlank(subBar, () -> "subBar cannot be blank");

        this.subBar = subBar;
        return this;
    }

    @Override
    public void configure(H composite) throws Exception {
        System.out.println("SubBarConfigurer configure composite with " + subBar);
    }
}
