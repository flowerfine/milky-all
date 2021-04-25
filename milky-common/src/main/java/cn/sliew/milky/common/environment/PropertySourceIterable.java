package cn.sliew.milky.common.environment;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Holder containing one or more {@link PropertySource} objects.
 */
public interface PropertySourceIterable extends Iterable<PropertySource<?>> {

    default Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Whether a property source with the given name is contained.
     *
     * @param name the {@link PropertySource#getName() name of the property source} to find
     */
    boolean containsPropertySource(String name);

    /**
     * Find the property source with the given name, {@code Optional.empty()} if not found.
     *
     * @param name the {@linkplain PropertySource#getName() name of the property source} to find
     */
    Optional<PropertySource<?>> getPropertySource(String name);

}
