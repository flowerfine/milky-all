package cn.sliew.milky.common.environment;

import java.util.Optional;
import java.util.function.Function;

public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {

    private final Optional<PropertySourceIterable> propertySources;

    public PropertySourcesPropertyResolver(PropertySourceIterable propertySources) {
        this.propertySources = Optional.ofNullable(propertySources);
    }

    @Override
    public boolean containsProperty(String key) {
        if (propertySources.isPresent()) {
            return propertySources.get().stream()
                    .filter(propertySource -> propertySource.containsProperty(key))
                    .findAny().isPresent();
        }
        return false;
    }

    @Override
    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(getProperty(key, Object::toString, false));
    }

    @Override
    public <R, T> Optional<T> getProperty(String key, Function<R, T> mappingFunction) {
        return Optional.ofNullable(getProperty(key, mappingFunction, true));
    }

    @Override
    protected Optional<String> getPropertyAsRawString(String key) {
        return Optional.ofNullable(getProperty(key, Object::toString, false));
    }

    protected <R, T> T getProperty(String key, Function<R, T> mappingFunction, boolean resolveNestedPlaceholders) {
        if (this.propertySources.isPresent()) {
            for (PropertySource<?> propertySource : this.propertySources.get()) {
                if (log.isTraceEnabled()) {
                    log.trace("Searching for key '" + key + "' in PropertySource '" +
                            propertySource.getName() + "'");
                }

                Object value = propertySource.getProperty(key);
                if (value != null) {
                    if (resolveNestedPlaceholders && value instanceof String) {
                        value = resolveNestedPlaceholders((String) value);
                    }
                    logKeyFound(key, propertySource, value);
                    return mappingFunction == null ? (T) value : mappingFunction.apply((R) value);
                }
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("Could not find key '" + key + "' in any property source");
        }
        return null;
    }

    /**
     * Log the given key as found in the given {@link PropertySource}, resulting in
     * the given value.
     */
    protected void logKeyFound(String key, PropertySource<?> propertySource, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("Found key '" + key + "' in PropertySource '" + propertySource.getName() +
                    "' with value of type " + value.getClass().getSimpleName());
        }
    }

}
