package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.primitives.*;
import cn.sliew.milky.common.util.JacksonUtil;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.sliew.milky.common.check.Ensures.checkArgument;
import static cn.sliew.milky.common.settings.PropertyHelper.isSensitive;

public class SettingHelper {

    private SettingHelper() {
        throw new IllegalStateException("no instance");
    }

    public static Setting<String> simpleString(String key, String defaultValue, Property... properties) {
        return new Setting<>(key, s -> defaultValue, Function.identity(), properties);
    }

    public static Setting<Float> floatSetting(String key, float defaultValue, Property... properties) {
        return new Setting<>(key, (s) -> Float.toString(defaultValue), Floats::parseFloat, properties);
    }

    public static Setting<Float> floatSetting(String key, float defaultValue, float minValue, Property... properties) {
        return new Setting<>(key, (s) -> Float.toString(defaultValue), (s) -> {
            float value = Floats.parseFloat(s);
            checkArgument(value >= minValue, () -> "Failed to parse value" +
                    (isSensitive(properties) ? "" : " [" + s + "]") + " for setting [" + key + "] must be >= " + minValue);
            return value;
        }, properties);
    }

    public static Setting<Double> doubleSetting(String key, double defaultValue, double minValue, Property... properties) {
        return doubleSetting(key, defaultValue, minValue, Double.POSITIVE_INFINITY, properties);
    }

    public static Setting<Double> doubleSetting(String key, double defaultValue, double minValue, double maxValue, Property... properties) {
        return new Setting<>(key, (s) -> Double.toString(defaultValue), (s) -> {
            final double d = Doubles.parseDouble(s);
            checkArgument(d >= minValue, () -> "Failed to parse value" +
                    (isSensitive(properties) ? "" : " [" + s + "]") + " for setting [" + key + "] must be >= " + minValue);
            checkArgument(d <= maxValue, () -> "Failed to parse value" +
                    (isSensitive(properties) ? "" : " [" + s + "]") + " for setting [" + key + "] must be <= " + maxValue);
            return d;
        }, properties);
    }

    public static Setting<Integer> intSetting(String key, int defaultValue, Property... properties) {
        return intSetting(key, defaultValue, Integer.MIN_VALUE, properties);
    }

    public static Setting<Integer> intSetting(String key, int defaultValue, int minValue, Property... properties) {
        return new Setting<>(key, (s) -> Integer.toString(defaultValue),
                (s) -> parseInt(s, minValue, key, isSensitive(properties)), properties);
    }

    public static Setting<Integer> intSetting(String key, int defaultValue, int minValue, int maxValue, Property... properties) {
        return new Setting<>(key, (s) -> Integer.toString(defaultValue),
                (s) -> parseInt(s, minValue, maxValue, key, isSensitive(properties)), properties);
    }

    public static Setting<Integer> intSetting(String key, int defaultValue, int minValue, Validator<Integer> validator,
                                              Property... properties) {
        return new Setting<>(key, Integer.toString(defaultValue),
                (s) -> parseInt(s, minValue, key, isSensitive(properties)), validator, properties);
    }

    public static Setting<Integer> intSetting(String key, Setting<Integer> fallbackSetting, int minValue, Property... properties) {
        return new Setting<>(key, fallbackSetting, (s) -> parseInt(s, minValue, key, isSensitive(properties)), properties);
    }

    public static Setting<Integer> intSetting(String key, Setting<Integer> fallbackSetting, int minValue, int maxValue,
                                              Property... properties) {
        return new Setting<>(key, fallbackSetting, (s) -> parseInt(s, minValue, maxValue, key, isSensitive(properties)), properties);
    }

    public static Setting<Integer> intSetting(String key, Setting<Integer> fallbackSetting, int minValue, Validator<Integer> validator,
                                              Property... properties) {
        return new Setting<>(new SimpleKey(key), fallbackSetting::getRaw, fallbackSetting,
                (s) -> parseInt(s, minValue, key, isSensitive(properties)), validator, properties);
    }

    public static int parseInt(String s, int minValue, String key) {
        return parseInt(s, minValue, Integer.MAX_VALUE, key);
    }

    public static int parseInt(String s, int minValue, int maxValue, String key) {
        return parseInt(s, minValue, maxValue, key, false);
    }

    public static int parseInt(String s, int minValue, String key, boolean sensitive) {
        return parseInt(s, minValue, Integer.MAX_VALUE, key, sensitive);
    }

    public static int parseInt(String s, int minValue, int maxValue, String key, boolean sensitive) {
        int value = Integers.parseInteger(s);
        checkArgument(value >= minValue, () -> "Failed to parse value" +
                (sensitive ? "" : " [" + s + "]") + " for setting [" + key + "] must be >= " + minValue);
        checkArgument(value <= maxValue, () -> "Failed to parse value" +
                (sensitive ? "" : " [" + s + "]") + " for setting [" + key + "] must be <= " + maxValue);
        return value;
    }

    public static Setting<Long> longSetting(String key, long defaultValue, long minValue, Property... properties) {
        return new Setting<>(key, (s) -> Long.toString(defaultValue), (s) -> parseLong(s, minValue, key, isSensitive(properties)),
                properties);
    }

    public static long parseLong(String s, long minValue, String key) {
        return parseLong(s, minValue, key, false);
    }

    public static long parseLong(String s, long minValue, String key, boolean sensitive) {
        long value = Longs.parseLong(s);
        checkArgument(value >= minValue, () -> "Failed to parse value" +
                (sensitive ? "" : " [" + s + "]") + " for setting [" + key + "] must be >= " + minValue);
        return value;
    }

    public static Setting<Boolean> boolSetting(String key, boolean defaultValue, Property... properties) {
        return new Setting<>(key, (s) -> Boolean.toString(defaultValue), b -> parseBoolean(b, key, isSensitive(properties)), properties);
    }

    public static Setting<Boolean> boolSetting(String key, Setting<Boolean> fallbackSetting, Property... properties) {
        return new Setting<>(key, fallbackSetting, b -> parseBoolean(b, key, isSensitive(properties)), properties);
    }

    public static Setting<Boolean> boolSetting(String key, Setting<Boolean> fallbackSetting, Validator<Boolean> validator,
                                               Property... properties) {
        return new Setting<>(new SimpleKey(key), fallbackSetting::getRaw, fallbackSetting, b -> parseBoolean(b, key,
                isSensitive(properties)), validator, properties);
    }

    public static Setting<Boolean> boolSetting(String key, boolean defaultValue, Validator<Boolean> validator, Property... properties) {
        return new Setting<>(key, Boolean.toString(defaultValue), b -> parseBoolean(b, key, isSensitive(properties)), validator, properties);
    }

    public static Setting<Boolean> boolSetting(String key, Function<Settings, String> defaultValueFn, Property... properties) {
        return new Setting<>(key, defaultValueFn, b -> parseBoolean(b, key, isSensitive(properties)), properties);
    }

    static boolean parseBoolean(String b, String key, boolean isFiltered) {
        try {
            return Booleans.parseBoolean(b);
        } catch (IllegalArgumentException ex) {
            if (isFiltered) {
                throw new IllegalArgumentException("Failed to parse value for setting [" + key + "]");
            } else {
                throw ex;
            }
        }
    }

    public static <T> Setting<List<T>> listSetting(
            final String key,
            final List<String> defaultStringValue,
            final Function<String, T> singleValueParser,
            final Property... properties) {
        return listSetting(key, defaultStringValue, singleValueParser, null, properties);
    }

    public static <T> Setting<List<T>> listSetting(
            final String key,
            final List<String> defaultStringValue,
            final Function<String, T> singleValueParser,
            final Validator<List<T>> validator,
            final Property... properties) {
        return listSetting(key, (s) -> defaultStringValue, singleValueParser, validator, properties);
    }

    // TODO this one's two argument get is still broken
    public static <T> Setting<List<T>> listSetting(
            final String key,
            final Setting<List<T>> fallbackSetting,
            final Function<String, T> singleValueParser,
            final Property... properties) {
        return listSetting(key, (s) -> parseableStringToList(fallbackSetting.getRaw(s)), fallbackSetting, singleValueParser, properties);
    }

    public static <T> Setting<List<T>> listSetting(
            final String key,
            final Function<Settings, List<String>> defaultStringValue,
            final Function<String, T> singleValueParser,
            final Property... properties) {
        return listSetting(key, defaultStringValue, null, singleValueParser, properties);
    }

    public static <T> Setting<List<T>> listSetting(
            final String key,
            final Function<Settings, List<String>> defaultStringValue,
            final Function<String, T> singleValueParser,
            final Validator<List<T>> validator,
            final Property... properties) {
        return listSetting(key, defaultStringValue, null, singleValueParser, validator, properties);
    }

    public static <T> Setting<List<T>> listSetting(
            final String key,
            final Function<Settings, List<String>> defaultStringValue,
            final Setting<List<T>> fallbackSetting,
            final Function<String, T> singleValueParser,
            final Property... properties) {
        return listSetting(key, defaultStringValue, fallbackSetting, singleValueParser, v -> {}, properties);
    }

    public static <T> Setting<List<T>> listSetting(
            final String key,
            final Function<Settings, List<String>> defaultStringValue,
            final Setting<List<T>> fallbackSetting,
            final Function<String, T> singleValueParser,
            final Validator<List<T>> validator,
            final Property... properties) {
        if (defaultStringValue.apply(Settings.EMPTY) == null) {
            throw new IllegalArgumentException("default value function must not return null");
        }
        Function<String, List<T>> parser = (s) ->
                parseableStringToList(s).stream().map(singleValueParser).collect(Collectors.toList());

        return new ListSetting<>(new ListKey(key), defaultStringValue, fallbackSetting, parser, validator, properties);
    }

    private static List<String> parseableStringToList(String parsableString) {
        return JacksonUtil.parseJsonArray(parsableString, String.class);
    }

    private static String arrayToParsableString(List<String> array) {
        return JacksonUtil.toJsonString(array);
    }

    public static Setting<Settings> groupSetting(String key, Property... properties) {
        return groupSetting(key, (s) -> {}, properties);
    }

    public static Setting<Settings> groupSetting(String key, Consumer<Settings> validator, Property... properties) {
        return new GroupSetting(key, validator, properties);
    }

    /**
     * This setting type allows to validate settings that have the same type and a common prefix. For instance feature.${type}=[true|false]
     * can easily be added with this setting. Yet, prefix key settings don't support updaters out of the box unless
     * {@link AffixSetting#getConcreteSetting(String)} is used to pull the updater.
     */
    public static <T> AffixSetting<T> prefixKeySetting(String prefix, Function<String, Setting<T>> delegateFactory) {
        BiFunction<String, String, Setting<T>> delegateFactoryWithNamespace = (ns, k) -> delegateFactory.apply(k);
        return affixKeySetting(new AffixKey(prefix), delegateFactoryWithNamespace);
    }

    /**
     * This setting type allows to validate settings that have the same type and a common prefix and suffix. For instance
     * storage.${backend}.enable=[true|false] can easily be added with this setting. Yet, affix key settings don't support updaters
     * out of the box unless {@link AffixSetting#getConcreteSetting(String)} is used to pull the updater.
     */
    public static <T> AffixSetting<T> affixKeySetting(String prefix, String suffix, Function<String, Setting<T>> delegateFactory,
                                                      AffixSetting.AffixSettingDependency... dependencies) {
        BiFunction<String, String, Setting<T>> delegateFactoryWithNamespace = (ns, k) -> delegateFactory.apply(k);
        return affixKeySetting(new AffixKey(prefix, suffix), delegateFactoryWithNamespace, dependencies);
    }

    public static <T> AffixSetting<T> affixKeySetting(String prefix, String suffix, BiFunction<String, String, Setting<T>> delegateFactory,
                                                      AffixSetting.AffixSettingDependency... dependencies) {
        Setting<T> delegate = delegateFactory.apply("_na_", "_na_");
        return new AffixSetting<>(new AffixKey(prefix, suffix), delegate, delegateFactory, dependencies);
    }

    private static <T> AffixSetting<T> affixKeySetting(AffixKey key, BiFunction<String, String, Setting<T>> delegateFactory,
                                                       AffixSetting.AffixSettingDependency... dependencies) {
        Setting<T> delegate = delegateFactory.apply("_na_", "_na_");
        return new AffixSetting<>(key, delegate, delegateFactory, dependencies);
    }

}
