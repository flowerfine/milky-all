package cn.sliew.milky.config.dsl;

import cn.sliew.milky.config.dsl.builder.CompositeConfig;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

class CompositeConfigTest extends MilkyTestCase {

    @Test
    void testCompositeConfig() throws Exception {
        CompositeConfig config = new CompositeConfig();
        config.foo()
                .foo("foo property")
                .and()
              .bar()
                .bar("bar property")
                .and()
              .subBar();

        config.getOrBuild();
    }

    @Test
    void testLambdaCompositeConfig() throws Exception {
        CompositeConfig config = new CompositeConfig();
        config.foo(fooConfigurer -> fooConfigurer.foo("foo property"))
                .subBar(Customizer.withDefaults())
                .bar(barConfigurer -> barConfigurer.bar("bar property"));
//                .subBar(Customizer.withDefaults());

        config.getOrBuild();
    }
}
