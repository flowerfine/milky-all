package cn.sliew.milky.event;

import java.util.EventObject;

public abstract class Event extends EventObject {

    private static final long serialVersionUID = 5965417997193586837L;

    private final long timestamp;

    public Event(Object source) {
        super(source);
        //fixme should use Clock
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
