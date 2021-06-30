package cn.sliew.milky.event.simple;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultEventPublisher<T> implements EventPublisher<T> {

    private final List<EventConsumer<T>> onEventConsumers = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<Class, List<EventConsumer<T>>> eventConsumerMap = new ConcurrentHashMap<>();
    private volatile boolean consumerRegistered;

    public boolean hasConsumers() {
        return consumerRegistered;
    }

    @Override
    public synchronized void registerEventConsumer(Class<T> eventClazz, EventConsumer<T> onEventConsumer) {
        this.consumerRegistered = true;
        this.eventConsumerMap.compute(eventClazz, (key, consumers) -> {
            if (consumers == null) {
                consumers = new LinkedList<>();
            }
            consumers.add(onEventConsumer);
            return consumers;
        });
    }

    @Override
    public void registerGenericConsumer(EventConsumer<T> onEventConsumer) {
        this.consumerRegistered = true;
        this.onEventConsumers.add(onEventConsumer);
    }

    @Override
    public boolean publishEvent(T event) {
        boolean consumed = false;
        if (!onEventConsumers.isEmpty()) {
            onEventConsumers.forEach(onEventConsumer -> onEventConsumer.onEvent(event));
            consumed = true;
        }
        if (!eventConsumerMap.isEmpty()) {
            List<EventConsumer<T>> eventConsumers = this.eventConsumerMap.get(event.getClass());
            if (eventConsumers != null && !eventConsumers.isEmpty()) {
                eventConsumers.forEach(consumer -> consumer.onEvent(event));
                consumed = true;
            }
        }
        return consumed;
    }
}
