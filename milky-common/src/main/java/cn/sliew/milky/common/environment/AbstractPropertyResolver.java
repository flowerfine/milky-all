package cn.sliew.milky.common.environment;

import java.util.function.Function;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public abstract class AbstractPropertyResolver implements PropertyResolver {

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

    /**
     * todo
     * @param text the String to resolve
     * @return
     */
    @Override
    public String resolvePlaceholders(String text) {
        return null;
    }

    /**
     * todo
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return null;
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
}
