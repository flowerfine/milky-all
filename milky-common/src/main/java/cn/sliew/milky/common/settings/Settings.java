package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.primitives.Booleans;
import cn.sliew.milky.common.primitives.Strings;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Settings {

    /**
     * The raw settings from the full key to raw string value.
     */
    private final Map<String, Object> settings;
    /**
     * Setting names found in this Settings for both string and secure settings.
     * This is constructed lazily in {@link #keySet()}.
     */
    private final Set<String> keys;

    private Settings(Map<String, Object> settings) {
        this.settings = settings;
        this.keys = Collections.unmodifiableSet(settings.keySet());
    }

    /**
     * Returns {@code true} if this settings object contains no settings.
     *
     * @return {@code true} if this settings object contains no settings
     */
    public boolean isEmpty() {
        return this.settings.isEmpty();
    }

    /**
     * Returns the number of settings in this settings object.
     */
    public int size() {
        return keySet().size();
    }

    /**
     * Returns the fully qualified setting names contained in this settings object.
     */
    public Set<String> keySet() {
        return this.keys;
    }

//    /**
//     * @return The direct keys of this settings
//     */
//    public Set<String> names() {
//        synchronized (firstLevelNames) {
//            if (firstLevelNames.get() == null) {
//                Stream<String> stream = settings.keySet().stream();
//                if (secureSettings != null) {
//                    stream = Stream.concat(stream, secureSettings.getSettingNames().stream());
//                }
//                Set<String> names = stream.map(k -> {
//                    int i = k.indexOf('.');
//                    if (i < 0) {
//                        return k;
//                    } else {
//                        return k.substring(0, i);
//                    }
//                }).collect(Collectors.toSet());
//                firstLevelNames.set(Collections.unmodifiableSet(names));
//            }
//        }
//        return firstLevelNames.get();
//    }

    /**
     * Returns <code>true</code> iff the given key has a value in this settings object.
     */
    public boolean hasValue(String key) {
        return settings.get(key) != null;
    }

    /**
     * Returns the setting value associated with the setting key.
     *
     * @param setting The setting key
     * @return The setting value, {@code null} if it does not exists.
     */
    public String get(String setting) {
        return toString(settings.get(setting));
    }

    /**
     * Returns the setting value associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public String get(String setting, String defaultValue) {
        String retVal = get(setting);
        return retVal == null ? defaultValue : retVal;
    }

    /**
     * A settings that are filtered (and key is removed) with the specified prefix.
     */
    public Settings getByPrefix(String prefix) {
        return new Settings(new FilteredMap(this.settings, (k) -> k.startsWith(prefix), prefix));
    }

    /**
     * Returns the settings mapped to the given setting name.
     */
    public Settings getAsSettings(String setting) {
        return getByPrefix(setting + ".");
    }

    /**
     * Returns a new settings object that contains all setting of the current one
     * filtered by the given settings key predicate.
     */
    public Settings filter(Predicate<String> predicate) {
        return new Settings(new FilteredMap(this.settings, predicate, null));
    }

    /**
     * Returns the setting value (as float) associated with the setting key.
     * If it does not exists, returns the default value provided.
     */
    public Float getAsFloat(String setting, Float defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException(
                    "Failed to parse float setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    /**
     * Returns the setting value (as double) associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public Double getAsDouble(String setting, Double defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException(
                    "Failed to parse double setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    /**
     * Returns the setting value (as int) associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public Integer getAsInt(String setting, Integer defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException("Failed to parse int setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    /**
     * Returns the setting value (as long) associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public Long getAsLong(String setting, Long defaultValue) {
        String sValue = get(setting);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(sValue);
        } catch (NumberFormatException e) {
            throw new SettingsException(
                    "Failed to parse long setting [" + setting + "] with value [" + sValue + "]", e);
        }
    }

    /**
     * Returns the setting value (as boolean) associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public Boolean getAsBoolean(String setting, Boolean defaultValue) {
        return Booleans.parseBoolean(get(setting), defaultValue);
    }

    /**
     * The values associated with a setting key as an immutable list.
     * <p/>
     * It will also automatically load a comma separated list under the settingPrefix and merge with
     * the numbered format.
     *
     * @param key The setting key to load the list by
     * @return The setting list values
     */
    public List<String> getAsList(String key) throws SettingsException {
        return getAsList(key, Collections.emptyList());
    }

    /**
     * The values associated with a setting key as an immutable list.
     * <p/>
     * If commaDelimited is true, it will automatically load a comma separated list
     * under the settingPrefix and merge with the numbered format.
     *
     * @param key The setting key to load the list by
     * @return The setting list values
     */
    public List<String> getAsList(String key, List<String> defaultValue) throws SettingsException {
        return getAsList(key, defaultValue, true);
    }

    /**
     * The values associated with a setting key as an immutable list.
     * <p/>
     * It will also automatically load a comma separated list under
     * the settingPrefix and merge with the numbered format.
     *
     * @param key            The setting key to load the list by
     * @param defaultValue   The default value to use if no value is specified
     * @param commaDelimited Whether to try to parse a string as a comma-delimited value
     * @return The setting list values
     */
    public List<String> getAsList(String key, List<String> defaultValue,
                                  Boolean commaDelimited) throws SettingsException {
        List<String> result = new ArrayList<>();
        final Object valueFromPrefix = settings.get(key);
        if (valueFromPrefix != null) {
            if (valueFromPrefix instanceof List) {
                return Collections.unmodifiableList((List<String>) valueFromPrefix);
            } else if (commaDelimited) {
                String[] strings = Strings.splitStringByCommaToArray(get(key));
                if (strings.length > 0) {
                    for (String string : strings) {
                        result.add(string.trim());
                    }
                }
            } else {
                result.add(get(key).trim());
            }
        }

        if (result.isEmpty()) {
            return defaultValue;
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns group settings for the given setting prefix.
     */
    public Map<String, Settings> getGroups(String settingPrefix) throws SettingsException {
        return getGroups(settingPrefix, false);
    }

    /**
     * Returns group settings for the given setting prefix.
     */
    public Map<String, Settings> getGroups(String settingPrefix, boolean ignoreNonGrouped) throws SettingsException {
        if (!Strings.hasLength(settingPrefix)) {
            throw new IllegalArgumentException("illegal setting prefix " + settingPrefix);
        }
        if (settingPrefix.charAt(settingPrefix.length() - 1) != '.') {
            settingPrefix = settingPrefix + ".";
        }
        return getGroupsInternal(settingPrefix, ignoreNonGrouped);
    }

    private Map<String, Settings> getGroupsInternal(String settingPrefix, boolean ignoreNonGrouped) throws SettingsException {
        Settings prefixSettings = getByPrefix(settingPrefix);
        Map<String, Settings> groups = new HashMap<>();
//        for (String groupName : prefixSettings.names()) {
//            Settings groupSettings = prefixSettings.getByPrefix(groupName + ".");
//            if (groupSettings.isEmpty()) {
//                if (ignoreNonGrouped) {
//                    continue;
//                }
//                throw new SettingsException("Failed to get setting group for [" + settingPrefix + "] setting prefix and setting ["
//                        + settingPrefix + groupName + "] because of a missing '.'");
//            }
//            groups.put(groupName, groupSettings);
//        }

        return Collections.unmodifiableMap(groups);
    }

    /**
     * Returns group settings for the given setting prefix.
     */
    public Map<String, Settings> getAsGroups() throws SettingsException {
        return getGroupsInternal("", false);
    }

    @Nullable
    private static String toString(Object o) {
        return Objects.toString(o, null);
    }

    /**
     * Returns a builder to be used in order to build settings.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder allowing to put different settings and then {@link #build()} an immutable
     * settings implementation. Use {@link Settings#builder()} in order to construct it.
     */
    public static class Builder {

        public static final Settings EMPTY_SETTINGS = new Builder().build();

        // we use a sorted map for consistent serialization when using getAsMap()
        private final Map<String, Object> map = new TreeMap<>();

        private Builder() {

        }

        public Set<String> keys() {
            return this.map.keySet();
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
        public Builder putNull(String key) {
            return put(key, (String) null);
        }

        /**
         * Sets a setting with the provided setting key and value.
         *
         * @param key   The setting key
         * @param value The setting value
         * @return The builder
         */
        public Builder put(String key, String value) {
            map.put(key, value);
            return this;
        }

        /**
         * Sets the setting with the provided setting key and the boolean value.
         *
         * @param setting The setting key
         * @param value   The boolean value
         * @return The builder
         */
        public Builder put(String setting, boolean value) {
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
        public Builder put(String setting, int value) {
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
        public Builder put(String setting, long value) {
            put(setting, String.valueOf(value));
            return this;
        }

        /**
         * Sets the setting with the provided setting key and the float value.
         *
         * @param setting The setting key
         * @param value   The float value
         * @return The builder
         */
        public Builder put(String setting, float value) {
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
        public Builder put(String setting, double value) {
            put(setting, String.valueOf(value));
            return this;
        }

        /**
         * Sets all the provided settings.
         *
         * @param settings the settings to set
         */
        public Builder put(Settings settings) {
            Map<String, Object> settingsMap = new HashMap<>(settings.settings);
            map.putAll(settingsMap);
            return this;
        }

        /**
         * Sets the setting with the provided setting key and an array of values.
         *
         * @param setting The setting key
         * @param values  The values
         * @return The builder
         */
        public Builder putList(String setting, String... values) {
            return putList(setting, Arrays.asList(values));
        }

        /**
         * Sets the setting with the provided setting key and a list of values.
         *
         * @param setting The setting key
         * @param values  The values
         * @return The builder
         */
        public Builder putList(String setting, List<String> values) {
            remove(setting);
            map.put(setting, new ArrayList<>(values));
            return this;
        }

        /**
         * Runs across all the settings set on this builder and
         * replaces {@code ${...}} elements in each setting with
         * another setting already set on this builder.
         */
        public Builder replacePropertyPlaceholders() {
            return replacePropertyPlaceholders(System::getenv);
        }

        // visible for testing
        Builder replacePropertyPlaceholders(Function<String, String> getenv) {
            PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder("${", "}", false);
            PropertyPlaceholder.PlaceholderResolver placeholderResolver = new PropertyPlaceholder.PlaceholderResolver() {
                @Override
                public String resolvePlaceholder(String placeholderName) {
                    final String value = getenv.apply(placeholderName);
                    if (value != null) {
                        return value;
                    }
                    return Settings.toString(map.get(placeholderName));
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

        public Builder copy(String key, Settings source) {
            return copy(key, key, source);
        }

        /**
         * copy.
         *
         * @param key       key
         * @param sourceKey source key
         * @param source    source settings
         */
        public Builder copy(String key, String sourceKey, Settings source) {
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
         * Builds a {@link Settings} (underlying uses {@link Settings}) based on everything
         * set on this builder.
         */
        public Settings build() {
            return new Settings(map);
        }
    }
}
