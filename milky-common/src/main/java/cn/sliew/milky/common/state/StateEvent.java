package cn.sliew.milky.common.state;

/**
 * The Event class holds information about the different events that can be handled
 * by the state machine. Events are prioritized depending on the importance of the event.
 * The priority model tries to ensure that old messages are handled before any new ones.
 */
public interface StateEvent extends Comparable {

    /**
     * Return type of this StateEvent
     *
     * @return type of this StateEvent
     */
    Enum type();

    /**
     * Return information object of this StateEvent
     *
     * @return information object of this StateEvent
     */
    Object data();
}