package cn.sliew.milky.event.simple;

public interface EventPublisher<T> {

    void registerEventConsumer(Class<T> eventClazz, EventConsumer<T> onEventConsumer);

    void registerGenericConsumer(EventConsumer<T> onEventConsumer);

    boolean publishEvent(T event);
}
