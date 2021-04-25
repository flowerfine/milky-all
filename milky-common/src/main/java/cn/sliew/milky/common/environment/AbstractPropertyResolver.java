package cn.sliew.milky.common.environment;

import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public abstract class AbstractPropertyResolver implements PropertyResolver {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private Optional<PropertyPlaceholderHelper> nonStrictHelper;

    private Optional<PropertyPlaceholderHelper> strictHelper;

    private boolean ignoreUnresolvableNestedPlaceholders = false;

    private String placeholderPrefix = "${";

    private String placeholderSuffix = "}";

    private String valueSeparator = ":";

    @Override
    public boolean containsProperty(String key) {
        return getProperty(key).isPresent();
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getProperty(key).orElse(defaultValue);
    }

    @Override
    public <R, T> T getProperty(String key, Function<R, T> mappingFunction, T defaultValue) {
        return getProperty(key, mappingFunction).orElse(defaultValue);
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        return getProperty(key).orElseThrow(() -> new IllegalStateException("Required key '" + key + "' not found"));
    }

    @Override
    public <R, T> T getRequiredProperty(String key, Function<R, T> mappingFunction) throws IllegalStateException {
        return getProperty(key, mappingFunction).orElseThrow(() -> new IllegalStateException("Required key '" + key + "' not found"));
    }

    @Override
    public String resolvePlaceholders(String text) {
        if (!this.nonStrictHelper.isPresent()) {
            this.nonStrictHelper = Optional.of(createPlaceholderHelper(true));
        }
        return doResolvePlaceholders(text, this.nonStrictHelper.get());
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (!this.strictHelper.isPresent()) {
            this.strictHelper = Optional.of(createPlaceholderHelper(false));
        }
        return doResolvePlaceholders(text, this.nonStrictHelper.get());
    }

    /**
     * Resolve placeholders within the given string.
     */
    protected String resolveNestedPlaceholders(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return (this.ignoreUnresolvableNestedPlaceholders ?
                resolvePlaceholders(value) : resolveRequiredPlaceholders(value));
    }

    private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix,
                this.valueSeparator, ignoreUnresolvablePlaceholders);
    }

    private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, this::getPropertyAsRawString);
    }

    @Override
    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = notBlank(placeholderPrefix, () -> "'placeholderPrefix' must not be blank");
    }

    @Override
    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = notBlank(placeholderSuffix, () -> "'placeholderSuffix' must not be blank");
    }

    @Override
    public void setValueSeparator(String valueSeparator) {
        this.placeholderSuffix = notBlank(valueSeparator, () -> "'valueSeparator' must not be blank");
    }

    /**
     * Retrieve the specified property as a raw String,
     * i.e. without resolution of nested placeholders.
     *
     * @param key the property name to resolve
     * @return the property value or {@code Optional.empty()} if none found
     */
    protected abstract Optional<String> getPropertyAsRawString(String key);

}
