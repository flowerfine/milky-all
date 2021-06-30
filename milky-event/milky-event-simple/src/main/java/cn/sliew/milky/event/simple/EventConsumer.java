package cn.sliew.milky.event.simple;

@FunctionalInterface
public interface EventConsumer<T> {

    void onEvent(T event);
}
