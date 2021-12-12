package cn.sliew.milky.property;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link JsonUnwrapped} 可以摊平一个 json 对象，并且可以自定义前后缀。借助这玩意可以替代 AffixSetting。
 * @param <T>
 */
public class AffixSetting<T> extends Setting<T> {

    private final AffixKey key;
    private final BiFunction<String, String, Setting<T>> delegateFactory;
    private final Set<AffixSettingDependency> dependencies;

    public AffixSetting(AffixKey key, Setting<T> delegate, BiFunction<String, String, Setting<T>> delegateFactory,
                        AffixSettingDependency... dependencies) {
        super(key, delegate.defaultValue, delegate.fallbackSetting.orElse(null), delegate.parser, v -> {});
        this.key = key;
        this.delegateFactory = delegateFactory;
        this.dependencies = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(dependencies)));
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

    /**
     * Get the raw list of dependencies. This method is exposed for testing purposes and {@link #getSettingsDependencies(String)}
     * should be preferred for most all cases.
     * @return the raw list of dependencies for this setting
     */
    public Set<AffixSettingDependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    @Override
    public Set<SettingDependency> getSettingsDependencies(String settingsKey) {
        if (dependencies.isEmpty()) {
            return Collections.emptySet();
        } else {
            String namespace = key.getNamespace(settingsKey);
            return dependencies.stream()
                    .map(s ->
                            new SettingDependency() {
                                @Override
                                public Setting<Object> getSetting() {
                                    return s.getSetting().getConcreteSettingForNamespace(namespace);
                                }

                                @Override
                                public void validate(final String key, final Object value, final Object dependency) {
                                    s.validate(key, value, dependency);
                                }
                            })
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Allows an affix setting to declare a dependency on another affix setting.
     */
    public interface AffixSettingDependency extends SettingDependency {

        @Override
        AffixSetting getSetting();

    }
}
