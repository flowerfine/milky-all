package cn.sliew.milky.thread;

import org.apache.logging.log4j.ThreadContext;

import java.io.Serializable;
import java.util.*;

public interface ThreadContextStack extends Collection<String>, Serializable {

    /**
     * Returns the element at the top of the stack.
     *
     * @return The element at the top of the stack.
     * @throws java.util.NoSuchElementException if the stack is empty.
     */
    String pop();

    /**
     * Returns the element at the top of the stack without removing it or null if the stack is empty.
     *
     * @return the element at the top of the stack or null if the stack is empty.
     */
    String peek();

    /**
     * Pushes an element onto the stack.
     *
     * @param message The element to add.
     */
    void push(String message);

    /**
     * Returns the number of elements in the stack.
     *
     * @return the number of elements in the stack.
     */
    int getDepth();

    /**
     * Returns all the elements in the stack in a List.
     *
     * @return all the elements in the stack in a List.
     */
    List<String> asList();

    /**
     * Trims elements from the end of the stack.
     *
     * @param depth The maximum number of items in the stack to keep.
     */
    void trim(int depth);

    /**
     * Returns a copy of the ContextStack.
     *
     * @return a copy of the ContextStack.
     */
    ThreadContextStack copy();

    /**
     * Returns a ContextStack with the same contents as this ContextStack or {@code null}. Attempts to modify the
     * returned stack may or may not throw an exception, but will not affect the contents of this ContextStack.
     *
     * @return a ContextStack with the same contents as this ContextStack or {@code null}.
     */
    ThreadContextStack getImmutableStackOrNull();

    /**
     * An empty read-only ThreadContextStack.
     */
    class EmptyThreadContextStack extends AbstractCollection<String> implements ThreadContextStack {

        private static final long serialVersionUID = 1L;

        private static final Iterator<String> EMPTY_ITERATOR = new EmptyIterator<>();

        @Override
        public String pop() {
            return null;
        }

        @Override
        public String peek() {
            return null;
        }

        @Override
        public void push(final String message) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getDepth() {
            return 0;
        }

        @Override
        public List<String> asList() {
            return Collections.emptyList();
        }

        @Override
        public void trim(final int depth) {
            // Do nothing
        }

        @Override
        public boolean equals(final Object o) {
            // Similar to java.util.Collections.EmptyList.equals(Object)
            return (o instanceof Collection) && ((Collection<?>) o).isEmpty();
        }

        @Override
        public int hashCode() {
            // Same as java.util.Collections.EmptyList.hashCode()
            return 1;
        }

        @Override
        public ThreadContextStack copy() {
            return this;
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(final String e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(final Collection<? extends String> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<String> iterator() {
            return EMPTY_ITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public ThreadContextStack getImmutableStackOrNull() {
            return this;
        }
    }

    class EmptyIterator<E> implements Iterator<E> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            throw new NoSuchElementException("This is an empty iterator!");
        }

        @Override
        public void remove() {
            // no-op
        }
    }

}
