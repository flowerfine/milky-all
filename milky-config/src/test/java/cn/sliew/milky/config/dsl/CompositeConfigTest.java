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

    /**
     *  注意：因为 SubBar 的属性是在 barConfigurer 中设置的，最后结果能否输出 subBar 的属性依赖于最后 BarConfigurer 和
     *  SubBarConfigurer 的执行顺序，而这个顺序经过测试并不稳定。
     */
    @Test
    void testLambdaCompositeConfig() throws Exception {
        CompositeConfig config = new CompositeConfig();
        config.foo(fooConfigurer -> fooConfigurer.foo("foo property"))
                .subBar(Customizer.withDefaults())
                .bar(barConfigurer -> barConfigurer.bar("bar property"));

        config.getOrBuild();
    }
}
