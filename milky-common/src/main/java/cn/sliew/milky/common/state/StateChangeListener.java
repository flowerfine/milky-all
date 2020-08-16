package cn.sliew.milky.common.state;

/**
 * Interface used to inform about changes in the state for a FSM.
 */
public interface StateChangeListener<T> {

    /**
     * A change of state has occurred for a FSM.
     *
     * @param source   the App Session that generated the change.
     * @param oldState Old state of FSM
     * @param newState New state of FSM
     */
    @SuppressWarnings("unchecked")
    void stateChanged(T source, State oldState, State newState);
}