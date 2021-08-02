package cn.sliew.milky.registry;

import java.time.ZonedDateTime;

abstract class AbstractRegistryEvent implements RegistryEvent {

    private final ZonedDateTime eventTime;

    AbstractRegistryEvent() {
        this.eventTime = ZonedDateTime.now();
    }

    @Override
    public ZonedDateTime getEventTime() {
        return eventTime;
    }
}
