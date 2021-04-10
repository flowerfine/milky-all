package cn.sliew.milky.event;

public interface EventListener<E extends Event> extends java.util.EventListener {

    void execute(E e);
}
