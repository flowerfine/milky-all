package cn.sliew.milky.common.environment;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.notBlank;

/**
 * The default implementation of the {@link PropertySourceIterable} interface.
 */
public class MutablePropertySources implements PropertySourceIterable {

    private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();

    /**
     * Create a new {@link MutablePropertySources} object.
     */
    public MutablePropertySources() {
    }

    /**
     * Create a new {@code MutablePropertySources} from the given propertySources
     * object, preserving the original order of contained {@code PropertySource} objects.
     */
    public MutablePropertySources(PropertySourceIterable propertySources) {
        this();
        propertySources.forEach(this::addLast);
    }

    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }

    @Override
    public Spliterator<PropertySource<?>> spliterator() {
        return Spliterators.spliterator(this.propertySourceList, 0);
    }

    @Override
    public Stream<PropertySource<?>> stream() {
        return this.propertySourceList.stream();
    }

    @Override
    public boolean containsPropertySource(String name) {
        return this.propertySourceList.stream()
                .filter(propertySource -> propertySource.getName().equals(name))
                .findAny().isPresent();
    }

    @Override
    public Optional<PropertySource<?>> getPropertySource(String name) {
        return this.propertySourceList.stream()
                .filter(propertySource -> propertySource.getName().equals(name))
                .findFirst();
    }

    /**
     * Add the given property source object with highest precedence.
     */
    public void addFirst(PropertySource<?> propertySource) {
        checkNotNull(propertySource);

        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            this.propertySourceList.add(0, propertySource);
        }
    }

    /**
     * Add the given property source object with lowest precedence.
     */
    public void addLast(PropertySource<?> propertySource) {
        checkNotNull(propertySource);

        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            this.propertySourceList.add(propertySource);
        }
    }

    /**
     * Add the given property source object with precedence immediately higher
     * than the named relative property source.
     */
    public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
        notBlank(relativePropertySourceName);
        checkNotNull(propertySource);

        assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            int index = assertPresentAndGetIndex(relativePropertySourceName);
            addAtIndex(index, propertySource);
        }
    }

    /**
     * Add the given property source object with precedence immediately lower
     * than the named relative property source.
     */
    public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
        notBlank(relativePropertySourceName);
        checkNotNull(propertySource);

        assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            int index = assertPresentAndGetIndex(relativePropertySourceName);
            addAtIndex(index + 1, propertySource);
        }
    }

    /**
     * Return the precedence of the given property source, {@code -1} if not found.
     */
    public int precedenceOf(PropertySource<?> propertySource) {
        checkNotNull(propertySource);

        return this.propertySourceList.indexOf(propertySource);
    }

    /**
     * Remove and return the property source with the given name, {@code null} if not found.
     * @param name the name of the property source to find and remove
     */
    public Optional<PropertySource<?>> remove(String name) {
        synchronized (this.propertySourceList) {
            int index = this.propertySourceList.indexOf(new MapPropertySource(name, Collections.emptyMap()));
            return (index != -1 ? Optional.of(this.propertySourceList.remove(index)) : Optional.empty());
        }
    }

    /**
     * Replace the property source with the given name with the given property source object.
     * @param name the name of the property source to find and replace
     * @param propertySource the replacement property source
     * @throws IllegalArgumentException if no property source with the given name is present
     * @see #containsPropertySource(String)
     */
    public void replace(String name, PropertySource<?> propertySource) {
        synchronized (this.propertySourceList) {
            int index = assertPresentAndGetIndex(name);
            this.propertySourceList.set(index, propertySource);
        }
    }

    /**
     * Return the number of {@link PropertySource} objects contained.
     */
    public int size() {
        return this.propertySourceList.size();
    }

    @Override
    public String toString() {
        return this.propertySourceList.toString();
    }

    /**
     * Remove the given property source if it is present.
     */
    protected void removeIfPresent(PropertySource<?> propertySource) {
        this.propertySourceList.remove(propertySource);
    }

    /**
     * Ensure that the given property source is not being added relative to itself.
     */
    protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
        String newPropertySourceName = propertySource.getName();
        if (relativePropertySourceName.equals(newPropertySourceName)) {
            throw new IllegalArgumentException(
                    "PropertySource named '" + newPropertySourceName + "' cannot be added relative to itself");
        }
    }

    /**
     * Add the given property source at a particular index in the list.
     */
    private void addAtIndex(int index, PropertySource<?> propertySource) {
        removeIfPresent(propertySource);
        this.propertySourceList.add(index, propertySource);
    }

    /**
     * Assert that the named property source is present and return its index.
     * @param name {@linkplain PropertySource#getName() name of the property source} to find
     * @throws IllegalArgumentException if the named property source is not present
     */
    private int assertPresentAndGetIndex(String name) {
        int index = this.propertySourceList.indexOf(new MapPropertySource(name, Collections.emptyMap()));
        if (index == -1) {
            throw new IllegalArgumentException("PropertySource named '" + name + "' does not exist");
        }
        return index;
    }

}
