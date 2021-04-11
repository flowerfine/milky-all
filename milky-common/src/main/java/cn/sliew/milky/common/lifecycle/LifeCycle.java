package cn.sliew.milky.common.lifecycle;

import java.time.Duration;

public interface LifeCycle {

    /**
     * Status of a life cycle.
     */
    enum State {
        /**
         * Object is in its initial state and not yet initialized.
         */
        INITIALIZING,
        /**
         * Initialized but not yet started.
         */
        INITIALIZED,
        /**
         * In the process of starting.
         */
        STARTING,
        /**
         * Has started.
         */
        STARTED,
        /**
         * Stopping is in progress.
         */
        STOPPING,
        /**
         * Has stopped.
         */
        STOPPED
    }

    /**
     * Gets the life-cycle state.
     *
     * @return the life-cycle state
     */
    State getState();

    void initialize();

    void start();

    boolean isStarted();

    void stop();

    boolean stop(Duration timeout);

    boolean isStopped();

}