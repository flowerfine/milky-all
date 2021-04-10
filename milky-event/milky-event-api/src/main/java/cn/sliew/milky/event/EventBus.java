package cn.sliew.milky.event;

import java.util.concurrent.Executor;

public interface EventBus {

    default Executor getExecutor() {
        return Runnable::run;
    }

    void fire(Event event);

    <E extends Event> void register(EventListener<E> listener);

    void register(Class<? extends Event> clazz, EventListener listener);
}
