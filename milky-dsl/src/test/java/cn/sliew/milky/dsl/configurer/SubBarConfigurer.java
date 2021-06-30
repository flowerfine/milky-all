package cn.sliew.milky.dsl.configurer;

import cn.sliew.milky.dsl.builder.CompositeBuilder;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class SubBarConfigurer extends AbstractCompositeConfigurer<SubBarConfigurer, CompositeBuilder> {

    private String subBar;

    public SubBarConfigurer subBar(String subBar) {
        notBlank(subBar, () -> "subBar cannot be blank");

        this.subBar = subBar;
        return this;
    }

    @Override
    public void configure(CompositeBuilder composite) throws Exception {
        composite.setSubBar(subBar);
        System.out.println("SubBarConfigurer configure composite with " + subBar);
    }
}
