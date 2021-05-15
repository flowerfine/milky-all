package cn.sliew.milky.config;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing a source of key/value property pairs with name.
 * <p>
 * The underlying {@linkplain #getSource() source object} may be of any type {@code T} that encapsulates
 * properties such as {@link java.util.Properties} objects, {@link java.util.Map} objects,
 * even with zookeeper remote config source.
 *
 * @param <T> the source type
 */
public interface PropertySource<T> {

    String getName();

    T getSource();

    boolean containsProperty(String property);

    Optional<Object> getProperty(String property);

    List<String> getAllPropertyNames();

}
