package cn.sliew.milky.test.extension.random.annotations;

import java.lang.annotation.*;

/**
 * Used to annotate constructor parameters for parameterized tests.
 * 
 * @see ParametersFactory
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Name {
  public String value();
}
