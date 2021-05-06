package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.parse.placeholder.PropertyPlaceholder;
import cn.sliew.milky.common.primitives.Strings;
import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
 * A builder allowing to put different settings and then {@link #build()} an immutable
 * settings implementation. Use {@link Settings#builder()} in order to construct it.
 */
public class SettingsBuilder {

    private static final Logger log = LoggerFactory.getLogger(SettingsBuilder.class);

    public static final Settings EMPTY_SETTINGS = new SettingsBuilder().build();

    // we use a sorted map for consistent serialization when using getAsMap()
    private final Map<String, Object> map = new TreeMap<>();

    SettingsBuilder() {

    }

    /**
     * Removes the provided setting from the internal map holding the current list of settings.
     */
    public String remove(String key) {
        return Settings.toString(map.remove(key));
    }

    /**
     * Returns a setting value based on the setting key.
     */
    public String get(String key) {
        return Settings.toString(map.get(key));
    }

    /**
     * Sets a null value for the given setting key.
     */
    public SettingsBuilder putNull(String key) {
        return put(key, null);
    }

    /**
     * Sets a setting with the provided setting key and value.
     *
     * @param key   The setting key
     * @param value The setting value
     * @return The builder
     */
    public SettingsBuilder put(String key, String value) {
        map.put(key, value);
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the float value.
     *
     * @param setting The setting key
     * @param value   The float value
     * @return The builder
     */
    public SettingsBuilder put(String setting, float value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the double value.
     *
     * @param setting The setting key
     * @param value   The double value
     * @return The builder
     */
    public SettingsBuilder put(String setting, double value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the int value.
     *
     * @param setting The setting key
     * @param value   The int value
     * @return The builder
     */
    public SettingsBuilder put(String setting, int value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the long value.
     *
     * @param setting The setting key
     * @param value   The long value
     * @return The builder
     */
    public SettingsBuilder put(String setting, long value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and the boolean value.
     *
     * @param setting The setting key
     * @param value   The boolean value
     * @return The builder
     */
    public SettingsBuilder put(String setting, boolean value) {
        put(setting, String.valueOf(value));
        return this;
    }

    /**
     * Sets the setting with the provided setting key and an array of values.
     *
     * @param setting The setting key
     * @param values  The values
     * @return The builder
     */
    public SettingsBuilder putList(String setting, String... values) {
        return putList(setting, Arrays.asList(values));
    }

    /**
     * Sets the setting with the provided setting key and a list of values.
     *
     * @param setting The setting key
     * @param values  The values
     * @return The builder
     */
    public SettingsBuilder putList(String setting, List<String> values) {
        remove(setting);
        map.put(setting, new ArrayList<>(values));
        return this;
    }

    /**
     * Sets all the provided settings.
     *
     * @param settings the settings to set
     */
    public SettingsBuilder put(Settings settings) {
        Map<String, Object> settingsMap = new HashMap<>(settings.settings);
        map.putAll(settingsMap);
        return this;
    }

    public SettingsBuilder copy(String key, Settings source) {
        return copy(key, key, source);
    }

    /**
     * copy.
     *
     * @param key       key
     * @param sourceKey source key
     * @param source    source settings
     */
    public SettingsBuilder copy(String key, String sourceKey, Settings source) {
        if (source.settings.containsKey(sourceKey) == false) {
            throw new IllegalArgumentException("source key not found in the source settings");
        }
        final Object value = source.settings.get(sourceKey);
        if (value instanceof List) {
            return putList(key, (List) value);
        } else if (value == null) {
            return putNull(key);
        } else {
            return put(key, Settings.toString(value));
        }
    }

    /**
     * Runs across all the settings set on this builder and
     * replaces {@code ${...}} elements in each setting with
     * another setting already set on this builder.
     */
    public SettingsBuilder replacePropertyPlaceholders() {
        return replacePropertyPlaceholders(System::getenv);
    }

    // visible for testing
    SettingsBuilder replacePropertyPlaceholders(Function<String, String> getenv) {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new PropertyPlaceholder.PlaceholderResolver() {
            @Override
            public Optional<String> resolvePlaceholder(String placeholderName) {
                final String value = getenv.apply(placeholderName);
                if (value != null) {
                    return Optional.of(value);
                }
                return Optional.ofNullable(Settings.toString(map.get(placeholderName)));
            }

            @Override
            public boolean shouldIgnoreMissing(String placeholderName) {
                return false;
            }

            @Override
            public boolean shouldRemoveMissingPlaceholder(String placeholderName) {
                return true;
            }
        };

        Iterator<Map.Entry<String, Object>> entryItr = map.entrySet().iterator();
        while (entryItr.hasNext()) {
            Map.Entry<String, Object> entry = entryItr.next();
            if (entry.getValue() == null) {
                // a null value obviously can't be replaced
                continue;
            }
            if (entry.getValue() instanceof List) {
                final ListIterator<String> li = ((List<String>) entry.getValue()).listIterator();
                while (li.hasNext()) {
                    final String settingValueRaw = li.next();
                    final String settingValueResolved = propertyPlaceholder.replacePlaceholders(settingValueRaw, placeholderResolver);
                    li.set(settingValueResolved);
                }
                continue;
            }

            String value = propertyPlaceholder.replacePlaceholders(Settings.toString(entry.getValue()), placeholderResolver);
            // if the values exists and has length, we should maintain it  in the map
            // otherwise, the replace process resolved into removing it
            if (Strings.hasLength(value)) {
                entry.setValue(value);
            } else {
                entryItr.remove();
            }
        }
        return this;
    }

    /**
     * Builds a {@link Settings} (underlying uses {@link Settings}) based on everything
     * set on this builder.
     */
    public Settings build() {
        return new Settings(map);
    }

}
