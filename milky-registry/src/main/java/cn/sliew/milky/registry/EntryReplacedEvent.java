package cn.sliew.milky.registry;

public class EntryReplacedEvent<E> extends AbstractRegistryEvent {

    private final E oldEntry;
    private final E newEntry;

    EntryReplacedEvent(E oldEntry, E newEntry) {
        super();
        this.oldEntry = oldEntry;
        this.newEntry = newEntry;
    }

    @Override
    public Type getEventType() {
        return Type.REPLACED;
    }

    /**
     * Returns the old entry.
     *
     * @return the old entry
     */

    public E getOldEntry() {
        return oldEntry;
    }

    /**
     * Returns the new entry.
     *
     * @return the new entry
     */
    public E getNewEntry() {
        return newEntry;
    }

    @Override
    public String toString() {
        return "EntryReplacedEvent{" +
                "oldEntry=" + oldEntry +
                ", newEntry=" + newEntry +
                ", eventTime=" + getEventTime() +
                '}';
    }
}