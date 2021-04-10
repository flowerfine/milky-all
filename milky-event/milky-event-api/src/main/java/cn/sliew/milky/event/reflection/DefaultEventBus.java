package cn.sliew.milky.event.reflection;

import cn.sliew.milky.event.Event;
import cn.sliew.milky.event.EventBus;
import cn.sliew.milky.event.EventListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.util.ReflectUtil.findParameterizedTypes;

public class DefaultEventBus implements EventBus {

    private final ConcurrentMap<Class, List<EventListener>> eventRepository = new ConcurrentHashMap<>(2);
    /**
     * todo 使用线程池组件调整替换它
     * todo 线程池拒绝策略和异常处理都没有处理
     */
    ExecutorService defaultExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1,
            Runtime.getRuntime().availableProcessors() + 1,
            5L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(126));


    @Override
    public void fire(Event event) {
        List<EventListener> eventListeners = findListener(event.getClass());
        eventListeners.stream().forEach(eventListener -> CompletableFuture.runAsync(() -> eventListener.execute(event), defaultExecutor));
    }

    @Override
    public <E extends Event> void register(EventListener<E> listener) {
        checkNotNull(listener, "event listener null");

        Optional<Class<? extends Event>> classOptional = determinateEventType(listener);
        Class<? extends Event> eventClazz = classOptional.orElse(Event.class);

        register(eventClazz, listener);
    }

    @Override
    public void register(Class<? extends Event> clazz, EventListener listener) {
        checkNotNull(clazz, "clazz null");
        checkNotNull(listener, "event listener null");

        List<EventListener> eventListeners = findListener(clazz);
        if (eventListeners.contains(listener)) {
            throw new IllegalStateException("can't register twice!");
        }
        eventListeners.add(listener);
    }

    private List<EventListener> findListener(Class<? extends Event> eventClass) {
        return eventRepository.computeIfAbsent(eventClass, key -> new LinkedList<>());
    }

    private Optional<Class<? extends Event>> determinateEventType(EventListener<?> listener) {
        return determinateEventType(listener.getClass());
    }

    private Optional<Class<? extends Event>> determinateEventType(Class<?> listenerClass) {
        Optional<Class<? extends Event>> eventType = Optional.empty();

        if (listenerClass != null && EventListener.class.isAssignableFrom(listenerClass)) {
            eventType = findParameterizedTypes(listenerClass).stream()
                    .map(this::determinateEventType)
                    .filter(Optional::isPresent)
                    .findAny().orElse(determinateEventType(listenerClass.getSuperclass()));
        }

        return eventType;
    }

    private Optional<Class<? extends Event>> determinateEventType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        if ((rawType instanceof Class) && EventListener.class.isAssignableFrom((Class) rawType)) {
            Optional<Type> optionalType = Stream.of(parameterizedType.getActualTypeArguments())
                    .filter(typeArgument -> typeArgument instanceof Class &&
                            Event.class.isAssignableFrom((Class) typeArgument))
                    .findFirst();
            if (optionalType.isPresent()) {
                return Optional.of((Class) optionalType.get());
            }
        }

        return Optional.empty();
    }
}
