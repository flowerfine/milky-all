package cn.sliew.milky.thread.context;

import cn.sliew.milky.thread.ThreadContext;
import cn.sliew.milky.thread.ThreadContextStack;

import java.util.*;

public class DefaultThreadContextStack implements ThreadContextStack {

    private static final long serialVersionUID = -4937612028391653349L;

    private final ThreadLocal<MutableThreadContextStack> STACK;

    public DefaultThreadContextStack() {
        STACK = createThreadLocalStack();
    }

    static ThreadLocal<MutableThreadContextStack> createThreadLocalStack() {
        return new InheritableThreadLocal<MutableThreadContextStack>() {
            @Override
            protected MutableThreadContextStack childValue(final MutableThreadContextStack parentValue) {
                return parentValue != null ?
                        new MutableThreadContextStack(parentValue.asList()) : new MutableThreadContextStack();
            }
        };
    }

    private MutableThreadContextStack getNonNullStackCopy() {
        final MutableThreadContextStack values = STACK.get();
        return (MutableThreadContextStack) (values == null ? new MutableThreadContextStack() : values.copy());
    }

    /**
     * preserve context for later store then clear current thread context stack.
     */
    public ThreadContext.StoredContext preserveContext() {
        MutableThreadContextStack threadContextStack = getNonNullStackCopy();
        STACK.set(new MutableThreadContextStack());
        return () -> STACK.set(threadContextStack);
    }

    @Override
    public ThreadContext.StoredContext storeContext() {
        MutableThreadContextStack threadContextStack = getNonNullStackCopy();
        return () -> STACK.set(threadContextStack);
    }

    @Override
    public boolean add(final String s) {
        final MutableThreadContextStack copy = getNonNullStackCopy();
        copy.add(s);
        copy.freeze();
        STACK.set(copy);
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends String> strings) {
        if (strings.isEmpty()) {
            return false;
        }
        final MutableThreadContextStack copy = getNonNullStackCopy();
        copy.addAll(strings);
        copy.freeze();
        STACK.set(copy);
        return true;
    }

    @Override
    public List<String> asList() {
        final MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return Collections.emptyList();
        }
        return values.asList();
    }

    @Override
    public void clear() {
        STACK.remove();
    }

    @Override
    public boolean contains(final Object o) {
        final MutableThreadContextStack values = STACK.get();
        return values != null && values.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> objects) {
        if (objects.isEmpty()) {
            return true;
        }
        final MutableThreadContextStack values = STACK.get();
        return values != null && values.containsAll(objects);
    }

    @Override
    public ThreadContextStack copy() {
        MutableThreadContextStack values;
        if ((values = STACK.get()) == null) {
            return new MutableThreadContextStack();
        }
        return values.copy();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ThreadContextStack)) {
            return false;
        }
        final ThreadContextStack other = (ThreadContextStack) obj;
        final MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return false;
        }
        return values.equals(other);
    }

    @Override
    public int getDepth() {
        final MutableThreadContextStack values = STACK.get();
        return values == null ? 0 : values.getDepth();
    }

    @Override
    public int hashCode() {
        final MutableThreadContextStack values = STACK.get();
        final int prime = 31;
        int result = 1;
        // Factor in the stack itself to compare vs. other implementors.
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
    public boolean isEmpty() {
        final MutableThreadContextStack values = STACK.get();
        return values == null || values.isEmpty();
    }

    @Override
    public Iterator<String> iterator() {
        final MutableThreadContextStack values = STACK.get();
        if (values == null) {
            final List<String> empty = Collections.emptyList();
            return empty.iterator();
        }
        return values.iterator();
    }

    @Override
    public String peek() {
        final MutableThreadContextStack values = STACK.get();
        if (values == null || values.size() == 0) {
            return "";
        }
        return values.peek();
    }

    @Override
    public String pop() {
        final MutableThreadContextStack values = STACK.get();
        if (values == null || values.size() == 0) {
            return "";
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        final String result = copy.pop();
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override
    public void push(final String message) {
        add(message);
    }

    @Override
    public boolean remove(final Object o) {
        final MutableThreadContextStack values = STACK.get();
        if (values == null || values.size() == 0) {
            return false;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        final boolean result = copy.remove(o);
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override
    public boolean removeAll(final Collection<?> objects) {
        if (objects.isEmpty()) {
            return false;
        }
        final MutableThreadContextStack values = STACK.get();
        if (values == null || values.isEmpty()) {
            return false;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        final boolean result = copy.removeAll(objects);
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override
    public boolean retainAll(final Collection<?> objects) {
        if (objects.isEmpty()) {
            return false;
        }
        final MutableThreadContextStack values = STACK.get();
        if (values == null || values.isEmpty()) {
            return false;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        final boolean result = copy.retainAll(objects);
        copy.freeze();
        STACK.set(copy);
        return result;
    }

    @Override
    public int size() {
        final MutableThreadContextStack values = STACK.get();
        return values == null ? 0 : values.size();
    }

    @Override
    public Object[] toArray() {
        final MutableThreadContextStack result = STACK.get();
        if (result == null) {
            return new String[0];
        }
        return result.toArray(new Object[result.size()]);
    }

    @Override
    public <T> T[] toArray(final T[] ts) {
        final MutableThreadContextStack result = STACK.get();
        if (result == null) {
            if (ts.length > 0) { // as per the contract of j.u.List#toArray(T[])
                ts[0] = null;
            }
            return ts;
        }
        return result.toArray(ts);
    }

    @Override
    public String toString() {
        final MutableThreadContextStack values = STACK.get();
        return values == null ? "[]" : values.toString();
    }

    @Override
    public void trim(final int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Maximum stack depth cannot be negative");
        }
        final MutableThreadContextStack values = STACK.get();
        if (values == null) {
            return;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        copy.trim(depth);
        copy.freeze();
        STACK.set(copy);
    }

    @Override
    public ThreadContextStack getImmutableStackOrNull() {
        return STACK.get();
    }
}
