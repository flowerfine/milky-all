package cn.sliew.milky.dsl.configurer;

import cn.sliew.milky.dsl.Composite;
import cn.sliew.milky.dsl.ConfigurableBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class BarConfigurer<H extends ConfigurableBuilder<Composite, H>> extends AbstractCompositeConfigurer<BarConfigurer<H>, H> {

    private String bar;

    public BarConfigurer<H> bar(String bar) {
        notBlank(bar, () -> "bar cannot be blank");

        this.bar = bar;
        return this;
    }

    @Override
    public void configure(H composite) throws Exception {
        System.out.println("BarConfigurer configure composite with " + bar);

        SubBarConfigurer subBarConfigurer = composite.getConfigurer(SubBarConfigurer.class);
        if (subBarConfigurer != null) {
            subBarConfigurer.subBar("sub bar property");
        }
    }
}
