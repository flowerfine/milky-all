package cn.sliew.milky.common.state;

/**
 * This interface must be extended by any class that should implement a certain state in the state machine.
 * <p>
 * https://github.com/RestComm/jdiameter/blob/master/core/jdiameter/api/src/main/java/org/jdiameter/api/app/State.java
 */
public interface State<T extends StateMachine> {

    /**
     * Action that should be taken each time this state is entered
     */
    void entryAction();

    /**
     * Action that should be taken each time this state is exited
     */
    void exitAction();

    /**
     * This method processed received event.
     *
     * @param fsm   the state machine
     * @param event the event to process.
     * @return true if event is processed
     */
    boolean processEvent(T fsm, StateEvent event);
}
