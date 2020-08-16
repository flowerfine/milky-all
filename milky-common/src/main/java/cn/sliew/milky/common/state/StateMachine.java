package cn.sliew.milky.common.state;

/**
 * The StateMachine lets you organize event handling,
 * if the order of the events are important to you.
 */
public interface StateMachine {

    /**
     * Add a new state change listener
     *
     * @param listener a reference to the listener that will get information about state changes.
     */
    void addStateChangeListener(StateChangeListener listener);

    /**
     * Remove a state change listener
     *
     * @param listener a reference to the listener that will get information about state changes.
     */
    void removeStateChangeListener(StateChangeListener listener);

    /**
     * Handle an event in the current state.
     *
     * @param event processing event
     * @return true if staterocessed
     */
    boolean handleEvent(StateEvent event);
}