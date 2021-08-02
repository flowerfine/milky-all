package cn.sliew.milky.registry;

import java.time.ZonedDateTime;

public interface RegistryEvent {

    /**
     * Returns the type of the Registry event.
     *
     * @return the type of the Registry event
     */
    Type getEventType();

    /**
     * Returns the creation time of Registry event.
     *
     * @return the creation time of Registry event
     */
    ZonedDateTime getEventTime();

    /**
     * Event types which are created by a CircuitBreaker.
     */
    enum Type {
        /**
         * An Event which informs that an entry has been added
         */
        ADDED,
        /**
         * An Event which informs that an entry has been removed
         */
        REMOVED,
        /**
         * An Event which informs that an entry has been replaced
         */
        REPLACED
    }
}
