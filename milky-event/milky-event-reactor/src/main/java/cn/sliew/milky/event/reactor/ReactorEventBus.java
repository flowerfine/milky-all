package cn.sliew.milky.event.reactor;

import cn.sliew.milky.event.Event;
import cn.sliew.milky.event.EventBus;
import cn.sliew.milky.event.EventListener;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedList;
import java.util.List;

/**
 * Reactor EventBus only suppoert specified Event type.
 *
 * @see <link href="https://projectreactor.io/2.x/reference/#reactor-bus">reactor-bus</link>
 */
public class ReactorEventBus<E extends Event> implements EventBus {

    private volatile List<EventListener<E>> eventListeners = new LinkedList<>();

    @Override
    public void fire(Event event) {
        Mono.justOrEmpty(event)
                .subscribeOn(Schedulers.fromExecutor(getExecutor()))
                .subscribe((e) -> eventListeners.stream().forEach(listener -> listener.execute((E) e)));
    }

    @Override
    public void register(EventListener listener) {
        LinkedList<EventListener<E>> listeners = new LinkedList<>(this.eventListeners);
        listeners.add(listener);
        this.eventListeners = listeners;
    }

    @Override
    public void register(Class<? extends Event> clazz, EventListener listener) {
        register(listener);
    }
}
