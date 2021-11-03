package cn.sliew.milky.common.settings;

import cn.sliew.milky.log.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static cn.sliew.milky.common.check.Requires.require;

/**
 * 1. property util method
 */
public class Setting<T> {

    private static final EnumSet<Property> EMPTY_PROPERTIES = EnumSet.noneOf(Property.class);

    private final Key key;
    private String description;
    protected final Function<Settings, String> defaultValue;
    protected final Optional<Setting<T>> fallbackSetting;
    protected final Function<String, T> parser;
    private final Validator<T> validator;

    private EnumSet<Property> properties;

    public Setting(String key, String defaultValue, Function<String, T> parser, Property... properties) {
        this(key, s -> defaultValue, parser, properties);
    }

    public Setting(String key, String defaultValue, Function<String, T> parser, Validator<T> validator, Property... properties) {
        this(new SimpleKey(key), s -> defaultValue, parser, validator, properties);
    }

    public Setting(String key, Setting<T> fallBackSetting, Function<String, T> parser, Property... properties) {
        this(new SimpleKey(key), fallBackSetting, parser, properties);
    }

    public Setting(String key, Function<Settings, String> defaultValue, Function<String, T> parser, Property... properties) {
        this(new SimpleKey(key), defaultValue, parser, properties);
    }

    public Setting(Key key, Function<Settings, String> defaultValue, Function<String, T> parser, Property... properties) {
        this(key, defaultValue, parser, v -> {}, properties);
    }

    public Setting(Key key, Function<Settings, String> defaultValue, Function<String, T> parser, Validator<T> validator, Property... properties) {
        this(key, defaultValue, null, parser, validator, properties);
    }

    public Setting(Key key, Setting<T> fallbackSetting, Function<String, T> parser, Property... properties) {
        this(key, fallbackSetting::getRaw, fallbackSetting, parser, v -> {}, properties);
    }

    /**
     * Creates a new Setting instance.
     * <p>
     * todo LineLength or Settings Source.
     *
     * @param key             key
     * @param defaultValue    default value
     * @param fallbackSetting fallback setting
     * @param parser          value parser
     * @param validator       value validator
     * @param properties      properties for this setting
     */
    public Setting(Key key,
                   Function<Settings, String> defaultValue,
                   Setting<T> fallbackSetting,
                   Function<String, T> parser,
                   Validator<T> validator,
                   Property... properties) {

        this.key = key;
        this.defaultValue = defaultValue;
        this.fallbackSetting = Optional.ofNullable(fallbackSetting);
        this.parser = parser;
        this.validator = validator;
        if (properties == null || properties.length == 0) {
            this.properties = EMPTY_PROPERTIES;
        } else {
            this.properties = EnumSet.copyOf(Arrays.asList(properties));
        }
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
     * Returns the setting properties
     *
     * @see Property
     */
    public EnumSet<Property> getProperties() {
        return properties;
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
                    "Failed to parse value '" + value + "' for setting '" + getKey() + "'", t);
        }
    }

    public final String getRaw(final Settings settings) {
        return innerGetRaw(settings);
    }

    String innerGetRaw(final Settings settings) {
        return settings.get(getKey(), defaultValue.apply(settings));
    }

    /**
     * Returns the default value for this setting.
     *
     * @param settings a settings object for settings that has a default value depending on another setting if available
     */
    public T getDefault(Settings settings) {
        return parser.apply(getDefaultRaw(settings));
    }

    /**
     * Returns the default value string representation for this setting.
     *
     * @param settings a settings object for settings that has a default value depending on another setting if available
     */
    public String getDefaultRaw(Settings settings) {
        return defaultValue.apply(settings);
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

    /**
     * Returns <code>true</code> iff the given key matches the settings key or if this setting is a group setting if the
     * given key is part of the settings group.
     *
     * @see #isGroupSetting()
     */
    public final boolean match(String toTest) {
        return key.match(toTest);
    }

    /**
     * Returns <code>true</code> iff this setting is a group setting. Group settings represent a set of settings rather than a single value.
     * The key, see {@link #getKey()}, in contrast to non-group settings is a prefix like {@code cluster.store.} that matches all settings
     * with this prefix.
     */
    boolean isGroupSetting() {
        return false;
    }

    boolean isListSetting() {
        return false;
    }

    /**
     * Returns a set of settings that are required at validation time. Unless all of the dependencies are present in the settings
     * object validation of setting must fail.
     */
    public Set<SettingDependency> getSettingsDependencies(final String key) {
        return Collections.emptySet();
    }

    public SettingUpdater<T> newUpdater(Consumer<T> updater, Consumer<T> validator, Logger logger) {
        return new Updater(updater, validator, logger);
    }

    public static <T> SettingBuilder<T> newSetting() {
        return new SettingBuilder<>();
    }

    private final class Updater implements SettingUpdater<T> {

        private final Consumer<T> updater;
        private final Consumer<T> validator;
        private final Logger log;

        public Updater(Consumer<T> updater, Consumer<T> validator, Logger log) {
            this.updater = updater;
            this.validator = validator;
            this.log = log;
        }

        @Override
        public boolean hasChanged(Settings current, Settings previous) {
            final String newValue = getRaw(current);
            final String value = getRaw(previous);

            require(isGroupSetting() == false, () -> "group settings must override this method");
            require(value != null, () -> "value was null but can't be unless default is null which is invalid");

            return value.equals(newValue) == false;
        }

        @Override
        public T getValue(Settings current, Settings previous) {
            final String newValue = getRaw(current);
            final String value = getRaw(previous);
            try {
                T inst = get(current);
                validator.accept(inst);
                return inst;
            } catch (Exception | AssertionError e) {
                throw new IllegalArgumentException(
                        String.format("illegal value can't update [%s] from [%s] to [%s]", getRawKey(), value, newValue),
                        e);
            }
        }

        @Override
        public void apply(T value, Settings current, Settings previous) {
            log.info("updating [{}] from [{}] to [{}]", getRawKey(), getRaw(previous), getRaw(current));
            updater.accept(value);
        }

        @Override
        public String toString() {
            return String.format("Updater for: %s", Setting.this.toString());
        }
    }

}
