package cn.sliew.milky.common.lifecycle;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractLifeCycle implements LifeCycle {

    private final Semaphore guard = new Semaphore(1);

    private final DelegatingLifeCycleListener lifeCycleListener = new DelegatingLifeCycleListener();

    private AtomicReference<State> stateHolder = new AtomicReference<>();

    @Override
    public State getState() {
        return stateHolder.get();
    }

    @Override
    public void addLifeCycleListener(LifeCycleListener listener) {
        lifeCycleListener.listeners.add(listener);
    }

    @Override
    public void removeLifeCycleListener(LifeCycleListener listener) {
        lifeCycleListener.listeners.remove(listener);
    }

    @Override
    public LifeCycleResult initialize() {
        if (!guard.tryAcquire()) {
            return LifeCycleResult.failure(new IllegalStateException("acquire lifecycle guard failed!"));
        }
        try {
            if (stateHolder.compareAndSet(null, State.INITIALIZING)) {
                lifeCycleListener.beforeInitialize(this);
                doInitialize();
                lifeCycleListener.afterInitialize(this);
                stateHolder.compareAndSet(State.INITIALIZING, State.INITIALIZED);
                return LifeCycleResult.success();
            } else {
                String message = String.format("wrong state '%s' which can't be initialized", getState());
                return LifeCycleResult.failure(new IllegalStateException(message));
            }
        } catch (Throwable throwable) {
            stateHolder.compareAndSet(State.INITIALIZING, null);
            return LifeCycleResult.failure(throwable);
        } finally {
            guard.release();
        }
    }

    @Override
    public LifeCycleSupportResult supportInitialize() {
        if (getState() == null) {
            return LifeCycleSupportResult.support("never initialized");
        } else {
            return LifeCycleSupportResult.unsupport(String.format("ever initialized, current state: '%s'", getState()));
        }
    }

    @Override
    public LifeCycleResult start() {
        if (!guard.tryAcquire()) {
            return LifeCycleResult.failure(new IllegalStateException("acquire lifecycle guard failed!"));
        }

        State state = getState();
        try {
            stateHolder.set(State.STARTING);
            lifeCycleListener.beforeStart(this);
            doStart();
            lifeCycleListener.afterStart(this);
            if (stateHolder.compareAndSet(State.STARTING, State.STARTED)) {
                return LifeCycleResult.success();
            } else {
                String message = String.format("move state to STARTED failed while start, which current state: '%s'", getState());
                return LifeCycleResult.failure(new IllegalStateException(message));
            }
        } catch (Throwable throwable) {
            stateHolder.set(state);
            return LifeCycleResult.failure(throwable);
        } finally {
            guard.release();
        }
    }

    @Override
    public LifeCycleSupportResult supportStart() {
        if (getState() == State.INITIALIZED) {
            return LifeCycleSupportResult.support("component ever initialized, ready to start");
        } else if (getState() == State.STOPPED) {
            return LifeCycleSupportResult.support("component ever stoped, ready to start");
        } else {
            return LifeCycleSupportResult.unsupport(String.format("only can be started while initialized or stoped, current state: '%s'", getState()));
        }
    }

    @Override
    public boolean isStarted() {
        return stateHolder.get() == State.STARTED;
    }

    @Override
    public LifeCycleResult stop() {
        if (!guard.tryAcquire()) {
            return LifeCycleResult.failure(new IllegalStateException("acquire lifecycle guard failed!"));
        }

        State state = getState();
        try {
            stateHolder.set(State.STOPPING);
            lifeCycleListener.beforeStop(this);
            doStop();
            lifeCycleListener.afterStop(this);
            if (stateHolder.compareAndSet(State.STOPPING, State.STOPPED)) {
                return LifeCycleResult.success();
            } else {
                String message = String.format("move state to STOPPED failed while stop, which current state: '%s'", getState());
                return LifeCycleResult.failure(new IllegalStateException(message));
            }
        } catch (Throwable throwable) {
            stateHolder.set(state);
            return LifeCycleResult.failure(throwable);
        } finally {
            guard.release();
        }
    }

    @Override
    public LifeCycleSupportResult supportStop() {
        if (getState() == State.INITIALIZED) {
            return LifeCycleSupportResult.support("component ever initialized, can stop directly");
        } else if (getState() == State.STARTED) {
            return LifeCycleSupportResult.support("component ever started, ready to stop");
        } else {
            return LifeCycleSupportResult.unsupport(String.format("only can be stoped while initialized or started, current state: '%s'", getState()));
        }
    }

    @Override
    public LifeCycleResult stop(Duration timeout) {
        if (!guard.tryAcquire()) {
            return LifeCycleResult.failure(new IllegalStateException("acquire lifecycle guard failed!"));
        }

        State state = getState();
        try {
            CompletableFuture<LifeCycleResult> future = CompletableFuture.supplyAsync(() -> stop());
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Throwable throwable) {
            stateHolder.set(state);
            return LifeCycleResult.failure(throwable);
        } finally {
            guard.release();
        }
    }

    @Override
    public boolean isStopped() {
        return stateHolder.get() == State.STOPPED;
    }

    protected abstract void doInitialize();

    protected abstract void doStart();

    protected abstract void doStop();

    private static class DelegatingLifeCycleListener<T extends LifeCycle> implements LifeCycleListener<T> {

        private final List<LifeCycleListener> listeners = new CopyOnWriteArrayList<>();

        @Override
        public void beforeInitialize(T component) {
            for (LifeCycleListener listener : listeners) {
                listener.beforeInitialize(component);
            }
        }

        @Override
        public void afterInitialize(T component) {
            for (LifeCycleListener listener : listeners) {
                listener.afterInitialize(component);
            }
        }

        @Override
        public void beforeStart(T component) {
            for (LifeCycleListener listener : listeners) {
                listener.beforeStart(component);
            }
        }

        @Override
        public void afterStart(T component) {
            for (LifeCycleListener listener : listeners) {
                listener.afterStart(component);
            }
        }

        @Override
        public void beforeStop(T component) {
            for (LifeCycleListener listener : listeners) {
                listener.beforeStop(component);
            }
        }

        @Override
        public void afterStop(T component) {
            for (LifeCycleListener listener : listeners) {
                listener.afterStop(component);
            }
        }
    }
}
