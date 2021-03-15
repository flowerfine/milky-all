package cn.sliew.milky.test.extension.random.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface ThreadLeakGroup {
  public static enum Group {
    /**
     * All JVM threads will be tracked. 
     * 
     * <p>WARNING: This option will not work
     * on IBM J9 because of livelock bugs in {@link Thread#getAllStackTraces()}.
     */
    ALL,

    /**
     * The "main" thread group and descendants will be tracked.   
     */
    MAIN, 

    /** 
     * Only per-suite test group and descendants will be tracked. 
     */
    TESTGROUP
  }

  Group value() default Group.MAIN;
}