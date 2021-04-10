package cn.sliew.milky.event.reactor;

import cn.sliew.milky.event.Event;
import cn.sliew.milky.event.EventBus;
import cn.sliew.milky.event.EventListener;

public class ReactorEventBus implements EventBus {

    @Override
    public void fire(Event event) {

    }

    @Override
    public <E extends Event> void register(EventListener<E> listener) {

    }

    @Override
    public void register(Class<? extends Event> clazz, EventListener listener) {

    }
}
