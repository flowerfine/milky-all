package cn.sliew.milky.common.lifecycle;

/**
 * {@link LifeCycle} listener.
 *
 * @param <T> life cycle component class
 */
public interface LifeCycleListener<T extends LifeCycle> {

    /**
     * Before component initialize.
     */
    default void beforeInitialize(T component) {

    }

    /**
     * After component initialize.
     */
    default void afterInitialize(T component) {

    }

    /**
     * Before component start.
     */
    default void beforeStart(T component) {

    }

    /**
     * After component start.
     */
    default void afterStart(T component) {

    }

    /**
     * Before component stop.
     */
    default void beforeStop(T component) {

    }

    /**
     * After component stop.
     */
    default void afterStop(T component) {

    }
}
