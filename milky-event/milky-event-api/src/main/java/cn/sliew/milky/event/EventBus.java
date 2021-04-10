package cn.sliew.milky.event;

public interface EventBus {

    void fire(Event event);

    <E extends Event> void register(EventListener<E> listener);

    void register(Class<? extends Event> clazz, EventListener listener);
}
