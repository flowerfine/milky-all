package cn.sliew.milky.registry;

public class EntryRemovedEvent<E> extends AbstractRegistryEvent {

    private E removedEntry;

    EntryRemovedEvent(E removedEntry) {
        this.removedEntry = removedEntry;
    }

    @Override
    public Type getEventType() {
        return Type.REMOVED;
    }

    /**
     * Returns the removed entry.
     *
     * @return the removed entry
     */
    public E getRemovedEntry() {
        return removedEntry;
    }

    @Override
    public String toString() {
        return "EntryAddedEvent{" +
                "removedEntry=" + removedEntry +
                ", eventTime=" + getEventTime() +
                '}';
    }
}