package cn.sliew.milky.thread;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class ThreadContext {

    public static final ThreadContextStack EMPTY_STACK = new ThreadContextStack.EmptyThreadContextStack();

    private static ThreadContextMap contextMap = new DefaultThreadContextMap();
    private static ThreadContextStack contextStack = new DefaultThreadContextStack();

    private ThreadContext() {
        // empty
    }

    /**
     * Puts a context value (the <code>value</code> parameter) as identified with the <code>key</code> parameter into
     * the current thread's context map.
     *
     * <p>
     * If the current thread does not have a context map it is created as a side effect.
     * </p>
     *
     * @param key   The key name.
     * @param value The key value.
     */
    public static void put(final String key, final String value) {
        contextMap.put(key, value);
    }

    /**
     * Puts a context value (the <code>value</code> parameter) as identified with the <code>key</code> parameter into
     * the current thread's context map if the key does not exist.
     *
     * <p>
     * If the current thread does not have a context map it is created as a side effect.
     * </p>
     *
     * @param key   The key name.
     * @param value The key value.
     */
    public static void putIfAbsent(final String key, final String value) {
        if (!contextMap.containsKey(key)) {
            contextMap.put(key, value);
        }
    }

    /**
     * Puts all given context map entries into the current thread's
     * context map.
     *
     * <p>If the current thread does not have a context map it is
     * created as a side effect.</p>
     *
     * @param m The map.
     */
    public static void putAll(final Map<String, String> m) {
        for (final Map.Entry<String, String> entry : m.entrySet()) {
            contextMap.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets the context value identified by the <code>key</code> parameter.
     *
     * <p>
     * This method has no side effects.
     * </p>
     *
     * @param key The key to locate.
     * @return The value associated with the key or null.
     */
    public static String get(final String key) {
        return contextMap.get(key);
    }

    /**
     * Removes the context value identified by the <code>key</code> parameter.
     *
     * @param key The key to remove.
     */
    public static void remove(final String key) {
        contextMap.remove(key);
    }

    /**
     * Removes the context values identified by the <code>keys</code> parameter.
     *
     * @param keys The keys to remove.
     */
    public static void removeAll(final Iterable<String> keys) {
        for (final String key : keys) {
            contextMap.remove(key);
        }
    }

    /**
     * Clears the context map.
     */
    public static void clearMap() {
        contextMap.clear();
    }

    /**
     * Clears the context map and stack.
     */
    public static void clearAll() {
        clearMap();
        clearStack();
    }

    /**
     * Determines if the key is in the context.
     *
     * @param key The key to locate.
     * @return True if the key is in the context, false otherwise.
     */
    public static boolean containsKey(final String key) {
        return contextMap.containsKey(key);
    }

    /**
     * Returns a mutable copy of current thread's context Map.
     *
     * @return a mutable copy of the context.
     */
    public static Map<String, String> getContext() {
        return contextMap.getCopy();
    }

    /**
     * Returns an immutable view of the current thread's context Map.
     *
     * @return An immutable view of the ThreadContext Map.
     */
    public static Map<String, String> getImmutableContext() {
        final Map<String, String> map = contextMap.getImmutableMapOrNull();
        return map == null ? Collections.emptyMap() : map;
    }

    public static ThreadContextMap getThreadContextMap() {
        return contextMap;
    }

    /**
     * Returns true if the Map is empty.
     *
     * @return true if the Map is empty, false otherwise.
     */
    public static boolean isEmpty() {
        return contextMap.isEmpty();
    }

    /**
     * Clears the stack for this thread.
     */
    public static void clearStack() {
        contextStack.clear();
    }

    /**
     * Returns a copy of this thread's stack.
     *
     * @return A copy of this thread's stack.
     */
    public static ThreadContextStack cloneStack() {
        return contextStack.copy();
    }

    /**
     * Gets an immutable copy of this current thread's context stack.
     *
     * @return an immutable copy of the ThreadContext stack.
     */
    public static ThreadContextStack getImmutableStack() {
        final ThreadContextStack result = contextStack.getImmutableStackOrNull();
        return result == null ? EMPTY_STACK : result;
    }

    /**
     * Sets this thread's stack.
     *
     * @param stack The stack to use.
     */
    public static void setStack(final Collection<String> stack) {
        if (stack.isEmpty()) {
            return;
        }
        contextStack.clear();
        contextStack.addAll(stack);
    }

    /**
     * Gets the current nesting depth of this thread's stack.
     *
     * @return the number of items in the stack.
     * @see #trim
     */
    public static int getDepth() {
        return contextStack.getDepth();
    }

    /**
     * Returns the value of the last item placed on the stack.
     *
     * <p>
     * The returned value is the value that was pushed last. If no context is available, then the empty string "" is
     * returned.
     * </p>
     *
     * @return String The innermost diagnostic context.
     */
    public static String pop() {
        return contextStack.pop();
    }

    /**
     * Looks at the last diagnostic context at the top of this NDC without removing it.
     *
     * <p>
     * The returned value is the value that was pushed last. If no context is available, then the empty string "" is
     * returned.
     * </p>
     *
     * @return String The innermost diagnostic context.
     */
    public static String peek() {
        return contextStack.peek();
    }

    /**
     * Pushes new diagnostic context information for the current thread.
     *
     * <p>
     * The contents of the <code>message</code> parameter is determined solely by the client.
     * </p>
     *
     * @param message The new diagnostic context information.
     */
    public static void push(final String message) {
        contextStack.push(message);
    }

    /**
     * Removes the diagnostic context for this thread.
     *
     * <p>
     * Each thread that created a diagnostic context by calling {@link #push} should call this method before exiting.
     * Otherwise, the memory used by the <b>thread</b> cannot be reclaimed by the VM.
     * </p>
     *
     * <p>
     * As this is such an important problem in heavy duty systems and because it is difficult to always guarantee that
     * the remove method is called before exiting a thread, this method has been augmented to lazily remove references
     * to dead threads. In practice, this means that you can be a little sloppy and occasionally forget to call
     * {@link #remove} before exiting a thread. However, you must call <code>remove</code> sometime. If you never call
     * it, then your application is sure to run out of memory.
     * </p>
     */
    public static void removeStack() {
        contextStack.clear();
    }

    public static void trim(final int depth) {
        contextStack.trim(depth);
    }

}
