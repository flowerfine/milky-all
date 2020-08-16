package cn.sliew.milky.common.component.fsm;

import cn.sliew.milky.common.state.StateEvent;

public class LifecycleEvent implements StateEvent {

    private final EventType eventType;
    private final Object data;

    public LifecycleEvent(EventType eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    @Override
    public Enum type() {
        return eventType;
    }

    @Override
    public Object data() {
        return data;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }
        LifecycleEvent that = (LifecycleEvent) o;
        return this.type().ordinal() - that.type().ordinal();
    }
}
