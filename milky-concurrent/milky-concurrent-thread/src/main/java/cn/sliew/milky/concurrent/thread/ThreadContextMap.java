package cn.sliew.milky.concurrent.thread;

import java.util.Map;

public interface ThreadContextMap {

    /**
     * preserve thread context map and set default context
     */
    ThreadContext.StoredContext preserveContext();

    /**
     * preserve thread context map without default context
     */
    ThreadContext.StoredContext storeContext();

    /**
     * Clears the context.
     */
    void clear();

    /**
     * Determines if the key is in the context.
     * @param key The key to locate.
     * @return True if the key is in the context, false otherwise.
     */
    boolean containsKey(final String key);

    /**
     * Gets the context identified by the <code>key</code> parameter.
     *
     * <p>This method has no side effects.</p>
     * @param key The key to locate.
     * @return The value associated with the key or null.
     */
    String get(final String key);

    /**
     * Gets a non-{@code null} mutable copy of current thread's context Map.
     * @return a mutable copy of the context.
     */
    Map<String, String> getCopy();

    /**
     * Returns an immutable view on the context Map or {@code null} if the context map is empty.
     * @return an immutable context Map or {@code null}.
     */
    Map<String, String> getImmutableMapOrNull();

    /**
     * Returns true if the Map is empty.
     * @return true if the Map is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Puts a context value (the <code>o</code> parameter) as identified
     * with the <code>key</code> parameter into the current thread's
     * context map.
     *
     * <p>If the current thread does not have a context map it is
     * created as a side effect.</p>
     * @param key The key name.
     * @param value The key value.
     */
    void put(final String key, final String value);

    /**
     * Removes the context identified by the <code>key</code>
     * parameter.
     * @param key The key to remove.
     */
    void remove(final String key);
}
