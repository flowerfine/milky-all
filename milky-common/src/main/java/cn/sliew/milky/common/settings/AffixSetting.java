package cn.sliew.milky.common.settings;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AffixSetting<T> extends Setting<T>  {

    public AffixSetting(Key key, Function<Settings, String> defaultValue, Setting<T> fallbackSetting, Function<String, T> parser, Validator<T> validator) {
        super(key, defaultValue, fallbackSetting, parser, validator);
    }

    private final AffixKey key;
    private final BiFunction<String, String, Setting<T>> delegateFactory;

    public AffixSetting(AffixKey key, Setting<T> delegate, BiFunction<String, String, Setting<T>> delegateFactory,) {
        super(key, delegate.defaultValue, delegate.parser, delegate.properties.toArray(new Property[0]));
        this.key = key;
        this.delegateFactory = delegateFactory;
    }

    @Override
    boolean isGroupSetting() {
        return true;
    }

    @Override
    public T get(Settings settings) {
        throw new UnsupportedOperationException("affix settings can't return values" +
                " use #getConcreteSetting to obtain a concrete setting");
    }

    @Override
    public String innerGetRaw(final Settings settings) {
        throw new UnsupportedOperationException("affix settings can't return values" +
                " use #getConcreteSetting to obtain a concrete setting");
    }

    @Override
    public Setting<T> getConcreteSetting(String key) {
        if (match(key)) {
            String namespace = this.key.getNamespace(key);
            return delegateFactory.apply(namespace, key);
        } else {
            throw new IllegalArgumentException("key [" + key + "] must match [" + getKey() + "] but didn't.");
        }
    }

    private Setting<T> getConcreteSetting(String namespace, String key) {
        if (match(key)) {
            return delegateFactory.apply(namespace, key);
        } else {
            throw new IllegalArgumentException("key [" + key + "] must match [" + getKey() + "] but didn't.");
        }
    }

    /**
     * Get a setting with the given namespace filled in for prefix and suffix.
     */
    public Setting<T> getConcreteSettingForNamespace(String namespace) {
        String fullKey = key.toConcreteKey(namespace).toString();
        return getConcreteSetting(namespace, fullKey);
    }

    @Override
    public void diff(Settings.Builder builder, Settings source, Settings defaultSettings) {
        matchStream(defaultSettings).forEach((key) -> getConcreteSetting(key).diff(builder, source, defaultSettings));
    }

    /**
     * Returns the namespace for a concrete setting. Ie. an affix setting with prefix: {@code search.} and suffix: {@code username}
     * will return {@code remote} as a namespace for the setting {@code cluster.remote.username}
     */
    public String getNamespace(Setting<T> concreteSetting) {
        return key.getNamespace(concreteSetting.getKey());
    }

    /**
     * Returns a stream of all concrete setting instances for the given settings. AffixSetting is only a specification, concrete
     * settings depend on an actual set of setting keys.
     */
    public Stream<Setting<T>> getAllConcreteSettings(Settings settings) {
        return matchStream(settings).distinct().map(this::getConcreteSetting);
    }

    /**
     * Returns distinct namespaces for the given settings
     */
    public Set<String> getNamespaces(Settings settings) {
        return settings.keySet().stream().filter(this::match).map(key::getNamespace).collect(Collectors.toSet());
    }

    /**
     * Returns a map of all namespaces to it's values give the provided settings
     */
    public Map<String, T> getAsMap(Settings settings) {
        Map<String, T> map = new HashMap<>();
        matchStream(settings).distinct().forEach(key -> {
            String namespace = this.key.getNamespace(key);
            Setting<T> concreteSetting = getConcreteSetting(namespace, key);
            map.put(namespace, concreteSetting.get(settings));
        });
        return Collections.unmodifiableMap(map);
    }

    private Stream<String> matchStream(Settings settings) {
        return settings.keySet().stream().filter(this::match).map(key::getConcreteString);
    }
}
