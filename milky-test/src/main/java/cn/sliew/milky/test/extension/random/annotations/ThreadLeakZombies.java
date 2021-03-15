package cn.sliew.milky.test.extension.random.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface ThreadLeakZombies {
    enum Consequence {
        /**
         * Continue execution with zombie threads running in the background as if
         * nothing happened. This is NOT a good idea because zombie threads may be
         * fiddling with the test instance or static fields. It is strongly
         * recommended to use a combination of
         * {@link ThreadLeakAction.Action#INTERRUPT} together with
         * {@link #IGNORE_REMAINING_TESTS} to enforce interruption of leaked threads
         * and if this does not succeed just ignore any tests further on.
         * <p>
         * For the reasons outlined above, this enum flag is marked as
         * {@link Deprecated}. It will be supported and will not be removed,
         * however.
         */
        @Deprecated
        CONTINUE,

        /**
         * Ignore any remaining tests once zombie threads have been detected. See
         * {@link #CONTINUE} for joint use with {@link ThreadLeakAction}.
         */
        IGNORE_REMAINING_TESTS;
    }

    ;

    Consequence value() default Consequence.IGNORE_REMAINING_TESTS;
}