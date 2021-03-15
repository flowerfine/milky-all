package cn.sliew.milky.test.extension.random.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface ThreadLeakAction {
  public static enum Action {
    /** Emit a warning using Java's logging system. */
    WARN,

    /** Try to {@link Thread#interrupt()} any leaked threads. */
    INTERRUPT;
  };

  Action [] value() default { Action.WARN, Action.INTERRUPT };
}