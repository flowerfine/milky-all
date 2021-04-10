package cn.sliew.milky.event.base;

import cn.sliew.milky.event.Event;
import cn.sliew.milky.event.EventListener;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractEventListener<E extends Event> implements EventListener<E> {

    private final AtomicInteger eventOccurs = new AtomicInteger(0);

    @Override
    public void execute(E event) {
        eventOccurs.getAndIncrement();
        handleEvent(event);
    }

    protected abstract void handleEvent(E event);

    public int getEventOccurs() {
        return eventOccurs.get();
    }

    protected void println(String message) {
        System.out.printf("[%s] %s\n", Thread.currentThread().getName(), message);
    }
}
