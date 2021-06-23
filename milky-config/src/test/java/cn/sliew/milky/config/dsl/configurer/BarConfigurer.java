package cn.sliew.milky.config.dsl.configurer;

import cn.sliew.milky.config.dsl.builder.CompositeBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class BarConfigurer<H extends CompositeBuilder<H>> extends AbstractCompositeConfigurer<BarConfigurer<H>, H> {

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
