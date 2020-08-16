package cn.sliew.milky.common.component.fsm;

import cn.sliew.milky.common.state.StateChangeListener;
import cn.sliew.milky.common.state.StateEvent;
import cn.sliew.milky.common.state.StateMachine;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LifecycleStateMachine implements StateMachine {

    protected ConcurrentLinkedQueue<StateChangeListener> listeners;

    private LifecycleState state;

    public LifecycleStateMachine() {
        this.listeners = new ConcurrentLinkedQueue<>();
    }

    void switchToNextState(LifecycleState newState) {
        // notify only when it's a new public state
        if (newState != state) {
            state.exitAction();
            state = newState;
            state.entryAction();
            for (StateChangeListener l : listeners) {
                l.stateChanged(this, state, newState);
            }
        }
    }

    @Override
    public boolean handleEvent(StateEvent event) {
        return state.processEvent(this, event);
    }

    public LifecycleState state() {
        return this.state;
    }

    /**
     * Returns {@code true} if the state is initialized.
     */
    public boolean initialized() {
        return state == InitializedState.INSTANCE;
    }

    /**
     * Returns {@code true} if the state is started.
     */
    public boolean started() {
        return state == StartedState.INSTANCE;
    }

    /**
     * Returns {@code true} if the state is stopped.
     */
    public boolean stopped() {
        return state == StoppedState.INSTANCE;
    }

    /**
     * Returns {@code true} if the state is closed.
     */
    public boolean closed() {
        return state == ClosedState.INSTANCE;
    }

    public boolean stoppedOrClosed() {
        LifecycleState state = this.state;
        return state == StoppedState.INSTANCE || state == ClosedState.INSTANCE;
    }

    public boolean canMoveToStarted() throws IllegalStateException {
        LifecycleState localState = this.state;
        if (localState == InitializedState.INSTANCE || localState == StoppedState.INSTANCE) {
            return true;
        }
        if (localState == StartedState.INSTANCE) {
            return false;
        }
        if (localState == ClosedState.INSTANCE) {
            throw new IllegalStateException("Can't move to started state when closed");
        }
        throw new IllegalStateException("Can't move to started with unknown state");
    }

    public boolean moveToStarted() throws IllegalStateException {
        LifecycleState localState = this.state;
        if (localState == InitializedState.INSTANCE || localState == StoppedState.INSTANCE) {
            state = StartedState.INSTANCE;
            return true;
        }
        if (localState == StartedState.INSTANCE) {
            return false;
        }
        if (localState == ClosedState.INSTANCE) {
            throw new IllegalStateException("Can't move to started state when closed");
        }
        throw new IllegalStateException("Can't move to started with unknown state");
    }

    public boolean canMoveToStopped() throws IllegalStateException {
        LifecycleState localState = state;
        if (localState == StartedState.INSTANCE) {
            return true;
        }
        if (localState == InitializedState.INSTANCE || localState == StoppedState.INSTANCE) {
            return false;
        }
        if (localState == ClosedState.INSTANCE) {
            throw new IllegalStateException("Can't move to stopped state when closed");
        }
        throw new IllegalStateException("Can't move to stopped with unknown state");
    }

    public boolean moveToStopped() throws IllegalStateException {
        LifecycleState localState = state;
        if (localState == StartedState.INSTANCE) {
            state = StoppedState.INSTANCE;
            return true;
        }
        if (localState == InitializedState.INSTANCE || localState == StoppedState.INSTANCE) {
            return false;
        }
        if (localState == ClosedState.INSTANCE) {
            throw new IllegalStateException("Can't move to stopped state when closed");
        }
        throw new IllegalStateException("Can't move to stopped with unknown state");
    }

    public boolean canMoveToClosed() throws IllegalStateException {
        LifecycleState localState = state;
        if (localState == ClosedState.INSTANCE) {
            return false;
        }
        if (localState == StartedState.INSTANCE) {
            throw new IllegalStateException("Can't move to closed before moving to stopped mode");
        }
        return true;
    }

    public boolean moveToClosed() throws IllegalStateException {
        LifecycleState localState = state;
        if (localState == ClosedState.INSTANCE) {
            return false;
        }
        if (localState == StartedState.INSTANCE) {
            throw new IllegalStateException("Can't move to closed before moving to stopped mode");
        }
        state = ClosedState.INSTANCE;
        return true;
    }


    @Override
    public void addStateChangeListener(StateChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeStateChangeListener(StateChangeListener listener) {
        listeners.remove(listener);
    }
}
