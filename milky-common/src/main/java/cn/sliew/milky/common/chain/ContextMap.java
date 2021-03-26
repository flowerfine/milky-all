package cn.sliew.milky.common.chain;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Convenience base class for {@link Context} implementations.
 *
 * @param <K> the type of keys maintained by the context associated with this context
 * @param <V> the type of mapped values
 */
public class ContextMap<K, V> extends ConcurrentHashMap<K, V> implements Context<K, V> {

    protected Logger logger;

    /**
     *
     */
    private static final long serialVersionUID = 20120724L;

    /**
     * Creates a new, empty Context with a default initial capacity, load factor, and concurrencyLevel.
     */
    public ContextMap() {
        this(LoggerFactory.getLogger(ContextMap.class));
    }

    /**
     * Creates a new, empty Context with a default initial capacity, load factor, and concurrencyLevel.
     */
    public ContextMap(Logger logger) {
        this.logger = logger;
    }

    /**
     * Creates a new, empty Context with the specified initial capacity, and with default load factor and concurrencyLevel
     *
     * @param initialCapacity the initial capacity.
     */
    public ContextMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a new, empty Context with the specified initial capacity, load factor, and concurrency level.
     *
     * @param initialCapacity the initial capacity.
     * @param loadFactor the load factor threshold, used to control resizing.
     * @param concurrencyLevel the estimated number of concurrently updating threads.
     */
    public ContextMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * Creates a new Context with the same mappings as the given map.
     *
     * @param t Map whose key-value pairs are added
     */
    public ContextMap(Map<? extends K, ? extends V> t) {
        super(t);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends V> T retrieve(K key) {
        V valueObject = get(key);
        if (valueObject == null) {
            return null;
        }
        // will throw ClassCastException if type are not assignable anyway
        @SuppressWarnings("unchecked")
        T value = (T) valueObject;
        return value;
    }

    @Override
    public Logger logger() {
        return logger;
    }
}