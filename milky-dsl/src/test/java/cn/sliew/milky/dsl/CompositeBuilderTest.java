package cn.sliew.milky.dsl;

import cn.sliew.milky.dsl.builder.CompositeBuilder;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

class CompositeBuilderTest extends MilkyTestCase {

    @Test
    void testCompositeConfig() throws Exception {
        CompositeBuilder config = new CompositeBuilder();
        config.foo()
                .foo("foo property")
                .and()
              .bar()
                .bar("bar property")
                .and()
              .subBar();

        final Composite composite = config.getOrBuild();
        System.out.println(composite);
    }

    @Test
    void testLambdaCompositeConfig() throws Exception {
        CompositeBuilder config = new CompositeBuilder();
        config.foo(fooConfigurer -> fooConfigurer.foo("foo property"))
                .subBar(Customizer.withDefaults())
                .bar(barConfigurer -> barConfigurer.bar("bar property"));
//                .subBar(Customizer.withDefaults());

        final Composite composite = config.getOrBuild();
        System.out.println(composite);
    }
}
