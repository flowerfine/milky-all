package cn.sliew.milky.test.extension.random.annotations;

import java.lang.annotation.*;

/**
 * Defines a list of starting seeds for a given test.
 * 
 * <p>Typically, you'll want to override the class's seed to make the test repeat a "fixed"
 * scenario in which the test was known to fail in the past. In addition, you may still permit
 * a randomized seed by adding a non-restricted {@link Seed} as in:</p>
 * <pre>
 * {@literal @}{@link Seeds}({
 *   {@literal @}{@link Seed}("deadbeef"),
 *   {@literal @}{@link Seed}("cafebabe"), 
 *   {@literal @}{@link Seed}()})
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Seeds {
  /**
   * A non-empty array of {@link Seed}s.
   */
  Seed [] value();
}