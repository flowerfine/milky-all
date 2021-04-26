package cn.sliew.milky.common.settings;

import java.util.*;
import java.util.function.Function;

/**
 * todo remove property
 */
public class Setting<T> {

    private final Key key;
    protected final Function<Settings, String> defaultValue;
    private final Optional<Setting<T>> fallbackSetting;
    private final Function<String, T> parser;
    private final Validator<T> validator;

    /**
     * LineLength.
     *
     * @param key             key
     * @param defaultValue    default value
     * @param fallbackSetting fallback setting
     * @param parser          value parser
     * @param validator       value validator
     */
    public Setting(Key key,
                   Function<Settings, String> defaultValue,
                   Setting<T> fallbackSetting,
                   Function<String, T> parser,
                   Validator<T> validator) {

        this.key = key;
        this.defaultValue = defaultValue;
        this.fallbackSetting = Optional.ofNullable(fallbackSetting);
        this.parser = parser;
        this.validator = validator;
    }

    /**
     * Returns the settings key or a prefix if this setting is a group setting.
     * <b>Note: this method should not be used to retrieve a value from a {@link Settings} object.
     * Use {@link #get(Settings)} instead</b>
     *
     * @see #isGroupSetting()
     */
    public final String getKey() {
        return key.toString();
    }

    /**
     * Returns the original representation of a setting key.
     */
    public final Key getRawKey() {
        return key;
    }

    /**
     * Returns the settings value. If the setting is not present in
     * the given settings object the default value is returned instead.
     */
    public T get(Settings settings) {
        return get(settings, true);
    }

    private T get(Settings settings, boolean validate) {
        String value = getRaw(settings);
        try {
            T parsed = parser.apply(value);
            if (validate) {
                final Iterator<Setting<T>> it = validator.settings();
                final Map<Setting<T>, T> map;
                if (it.hasNext()) {
                    map = new HashMap<>();
                    while (it.hasNext()) {
                        final Setting<T> setting = it.next();
                        // we have to disable validation or we will stack overflow
                        map.put(setting, setting.get(settings, false));
                    }
                } else {
                    map = Collections.emptyMap();
                }
                validator.validate(parsed);
                validator.validate(parsed, map);
            }
            return parsed;
        } catch (Exception t) {
            throw new IllegalArgumentException(
                    "Failed to parse value [" + value + "] for setting [" + getKey() + "]", t);
        }
    }

    public final String getRaw(final Settings settings) {
        return innerGetRaw(settings);
    }

    /**
     * The underlying implementation for {@link #getRaw(Settings)}.
     * Setting specializations can override this as needed to convert the
     * actual settings value to raw strings.
     *
     * @param settings the settings instance
     * @return the raw string representation of the setting value
     */
    String innerGetRaw(final Settings settings) {
        return settings.get(getKey(), defaultValue.apply(settings));
    }

    /**
     * Returns the default value for this setting.
     *
     * @param settings a settings object for settings that has a default value depending on another setting if available
     */
    public T getDefault(Settings settings) {
        return parser.apply(defaultValue.apply(settings));
    }

    /**
     * Returns true if and only if this setting is present in the given settings instance.
     * Note that fallback settings are excluded.
     *
     * @param settings the settings
     * @return true if the setting is present in the given settings instance, otherwise false
     */
    public boolean exists(final Settings settings) {
        return settings.keySet().contains(getKey());
    }

    /**
     * Returns true if and only if this setting including fallback settings
     * is present in the given settings instance.
     *
     * @param settings the settings
     * @return true if the setting including fallback settings is present in
     * the given settings instance, otherwise false
     */
    public boolean existsOrFallbackExists(final Settings settings) {
        return settings.keySet().contains(getKey())
                || (fallbackSetting.isPresent() && fallbackSetting.get().existsOrFallbackExists(settings));
    }


}
