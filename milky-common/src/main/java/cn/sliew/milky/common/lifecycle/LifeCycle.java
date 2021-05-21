package cn.sliew.milky.common.lifecycle;

import java.time.Duration;

/**
 * Life cycle component, which useful for handle component initialize, start and stop.
 */
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

    void addLifeCycleListener(LifeCycleListener listener);

    void removeLifeCycleListener(LifeCycleListener listener);

    LifeCycleResult initialize();

    LifeCycleSupportResult supportInitialize();

    LifeCycleResult start();

    LifeCycleSupportResult supportStart();

    boolean isStarted();

    LifeCycleResult stop();

    LifeCycleSupportResult supportStop();

    LifeCycleResult stop(Duration timeout);

    boolean isStopped();

    class LifeCycleResult {

        private final boolean status;
        private final Throwable throwable;

        private LifeCycleResult(boolean status, Throwable throwable) {
            this.status = status;
            this.throwable = throwable;
        }

        public boolean isSuccess() {
            return status;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public static LifeCycleResult success() {
            return new LifeCycleResult(true, null);
        }

        public static LifeCycleResult failure(Throwable throwable) {
            return new LifeCycleResult(false, throwable);
        }
    }

    class LifeCycleSupportResult {

        private final boolean support;

        private final String reason;

        private LifeCycleSupportResult(boolean support, String reason) {
            this.support = support;
            this.reason = reason;
        }

        public boolean support() {
            return support;
        }

        public String reason() {
            return reason;
        }

        public static LifeCycleSupportResult support(String reason) {
            return new LifeCycleSupportResult(true, reason);
        }

        public static LifeCycleSupportResult unsupport(String reason) {
            return new LifeCycleSupportResult(false, reason);
        }
    }

}