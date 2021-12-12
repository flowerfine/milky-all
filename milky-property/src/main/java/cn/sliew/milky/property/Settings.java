package cn.sliew.milky.property;

import cn.sliew.milky.common.collect.SetOnce;
import cn.sliew.milky.common.primitives.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Settings {

    public static final Settings EMPTY = new SettingsBuilder().build();

    /**
     * The raw settings from the full key to raw string value.
     */
    final Map<String, Object> settings;

    /**
     * Setting names found in this Settings.
     */
    private final Set<String> keys;

    /**
     * The first level of setting names. This is constructed lazily in {@link #names()}.
     */
    private final SetOnce<Set<String>> firstLevelNames = new SetOnce<>();

    Settings(Map<String, Object> settings) {
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

    /**
     * @return The direct keys of this settings
     */
    public Set<String> names() {
        if (firstLevelNames.get() == null) {
            synchronized (firstLevelNames) {
                if (firstLevelNames.get() == null) {
                    Stream<String> stream = settings.keySet().stream();
                    Set<String> names = stream.map(k -> {
                        int i = k.indexOf('.');
                        if (i < 0) {
                            return k;
                        } else {
                            return k.substring(0, i);
                        }
                    }).collect(Collectors.toSet());
                    firstLevelNames.set(Collections.unmodifiableSet(names));
                }
            }
        }
        return firstLevelNames.get();
    }

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
        return Optional.ofNullable(get(setting)).orElse(defaultValue);
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
        return Floats.parseFloat(get(setting), defaultValue);
    }

    /**
     * Returns the setting value (as double) associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public Double getAsDouble(String setting, Double defaultValue) {
        return Doubles.parseDouble(get(setting), defaultValue);
    }

    /**
     * Returns the setting value (as int) associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public Integer getAsInt(String setting, Integer defaultValue) {
        return Integers.parseInteger(get(setting), defaultValue);
    }

    /**
     * Returns the setting value (as long) associated with the setting key. If it does not exists,
     * returns the default value provided.
     */
    public Long getAsLong(String setting, Long defaultValue) {
        return Longs.parseLong(get(setting), defaultValue);
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
    public Map<String, Settings> getAsGroups() throws SettingsException {
        return getGroups("");
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
        for (String groupName : prefixSettings.names()) {
            Settings groupSettings = prefixSettings.getByPrefix(groupName + ".");
            if (groupSettings.isEmpty()) {
                if (ignoreNonGrouped) {
                    continue;
                }
                throw new SettingsException("Failed to get setting group for [" + settingPrefix + "] setting prefix and setting ["
                        + settingPrefix + groupName + "] because of a missing '.'");
            }
            groups.put(groupName, groupSettings);
        }

        return Collections.unmodifiableMap(groups);
    }

    /**
     * Returns the settings as delimited string.
     */
    public String toDelimitedString(char delimiter) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(delimiter);
        }
        return sb.toString();
    }

    static String toString(Object o) {
        return Objects.toString(o, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Settings that = (Settings) o;
        return Objects.equals(settings, that.settings);
    }

    @Override
    public int hashCode() {
        return settings != null ? settings.hashCode() : 0;
    }

    /**
     * Returns a builder to be used in order to build settings.
     */
    public static SettingsBuilder builder() {
        return new SettingsBuilder();
    }

}
