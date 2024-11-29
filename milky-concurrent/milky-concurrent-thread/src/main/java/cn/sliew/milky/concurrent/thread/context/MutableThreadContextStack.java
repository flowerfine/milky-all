package cn.sliew.milky.concurrent.thread.context;

import cn.sliew.milky.concurrent.thread.ThreadContext;
import cn.sliew.milky.concurrent.thread.ThreadContextStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MutableThreadContextStack implements ThreadContextStack {

    private static final long serialVersionUID = 50505011L;

    /**
     * The underlying list (never null).
     */
    private final List<String> list;
    private boolean frozen;

    /**
     * Constructs an empty MutableThreadContextStack.
     */
    public MutableThreadContextStack() {
        this(new ArrayList<>());
    }

    /**
     * Constructs a new instance.
     *
     * @param list The list of items in the stack.
     */
    public MutableThreadContextStack(final List<String> list) {
        this.list = new ArrayList<>(list);
    }

    private MutableThreadContextStack(final MutableThreadContextStack stack) {
        this.list = new ArrayList<>(stack.list);
    }

    private void checkInvariants() {
        if (frozen) {
            throw new UnsupportedOperationException("context stack has been frozen");
        }
    }

    @Override
    public ThreadContext.StoredContext preserveContext() {
        return () -> {};
    }

    @Override
    public ThreadContext.StoredContext storeContext() {
        return () -> {};
    }

    @Override
    public String pop() {
        checkInvariants();
        if (list.isEmpty()) {
            return null;
        }
        return list.remove(list.size() - 1);
    }

    @Override
    public String peek() {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    @Override
    public void push(final String message) {
        checkInvariants();
        list.add(message);
    }

    @Override
    public int getDepth() {
        return list.size();
    }

    @Override
    public List<String> asList() {
        return list;
    }

    @Override
    public void trim(final int depth) {
        checkInvariants();
        if (depth < 0) {
            throw new IllegalArgumentException("Maximum stack depth cannot be negative");
        }
        if (list == null) {
            return;
        }
        final List<String> copy = new ArrayList<>(list.size());
        final int count = Math.min(depth, list.size());
        for (int i = 0; i < count; i++) {
            copy.add(list.get(i));
        }
        list.clear();
        list.addAll(copy);
    }

    @Override
    public ThreadContextStack copy() {
        return new MutableThreadContextStack(this);
    }

    @Override
    public void clear() {
        checkInvariants();
        list.clear();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] ts) {
        return list.toArray(ts);
    }

    @Override
    public boolean add(final String s) {
        checkInvariants();
        return list.add(s);
    }

    @Override
    public boolean remove(final Object o) {
        checkInvariants();
        return list.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> objects) {
        return list.containsAll(objects);
    }

    @Override
    public boolean addAll(final Collection<? extends String> strings) {
        checkInvariants();
        return list.addAll(strings);
    }

    @Override
    public boolean removeAll(final Collection<?> objects) {
        checkInvariants();
        return list.removeAll(objects);
    }

    @Override
    public boolean retainAll(final Collection<?> objects) {
        checkInvariants();
        return list.retainAll(objects);
    }

    @Override
    public String toString() {
        return String.valueOf(list);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.list == null) ? 0 : this.list.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof org.apache.logging.log4j.spi.ThreadContextStack)) {
            return false;
        }
        final ThreadContextStack other = (ThreadContextStack) obj;
        final List<String> otherAsList = other.asList();
        if (this.list == null) {
            if (otherAsList != null) {
                return false;
            }
        } else if (!this.list.equals(otherAsList)) {
            return false;
        }
        return true;
    }

    @Override
    public ThreadContextStack getImmutableStackOrNull() {
        return copy();
    }

    /**
     * "Freezes" this context stack so it becomes immutable: all mutator methods will throw an exception from now on.
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * Returns whether this context stack is frozen.
     *
     * @return whether this context stack is frozen.
     */
    public boolean isFrozen() {
        return frozen;
    }
}
