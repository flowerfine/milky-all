package cn.sliew.milky.dsl.configurer;

import cn.sliew.milky.dsl.builder.CompositeBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class BarConfigurer extends AbstractCompositeConfigurer<BarConfigurer, CompositeBuilder> {

    private String bar;

    public BarConfigurer bar(String bar) {
        notBlank(bar, () -> "bar cannot be blank");

        this.bar = bar;
        return this;
    }

    @Override
    public void configure(CompositeBuilder composite) throws Exception {
        composite.setBar(bar);
        System.out.println("BarConfigurer configure composite with " + bar);

        SubBarConfigurer subBarConfigurer = composite.getConfigurer(SubBarConfigurer.class);
        if (subBarConfigurer != null) {
            subBarConfigurer.subBar("sub bar property");
        }
    }
}
