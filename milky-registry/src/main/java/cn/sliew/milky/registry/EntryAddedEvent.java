package cn.sliew.milky.registry;

public class EntryAddedEvent<E> extends AbstractRegistryEvent {

    private E addedEntry;

    EntryAddedEvent(E addedEntry) {
        this.addedEntry = addedEntry;
    }

    @Override
    public Type getEventType() {
        return Type.ADDED;
    }

    public E getAddedEntry() {
        return addedEntry;
    }

    @Override
    public String toString() {
        return "EntryAddedEvent{" +
                "addedEntry=" + addedEntry +
                ", eventTime=" + getEventTime() +
                '}';
    }
}