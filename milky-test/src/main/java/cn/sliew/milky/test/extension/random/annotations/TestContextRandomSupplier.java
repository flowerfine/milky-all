package cn.sliew.milky.test.extension.random.annotations;


import cn.sliew.milky.test.extension.random.RandomSupplier;
import cn.sliew.milky.test.extension.random.RandomizedContext;

import java.lang.annotation.*;
import java.util.Random;

/**
 * A supplier of {@link Random} instances for the {@link RandomizedContext}. The supplier class must declare
 * a public no-arg constructor.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface TestContextRandomSupplier {
  Class<? extends RandomSupplier> value();
}
