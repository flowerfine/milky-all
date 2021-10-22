package cn.sliew.milky.common.settings;

import java.util.List;
import java.util.function.Function;

public class ListSetting<T> extends Setting<List<T>> {

    private final Function<Settings, List<String>> defaultStringValue;

    /**
     * todo 将 list 转变成 json
     */
    public ListSetting(ListKey key, Function<Settings, List<String>> defaultStringValue, Setting<List<T>> fallbackSetting, Function<String, List<T>> parser, Validator<List<T>> validator, Property... properties) {
        super(key, s -> defaultStringValue.apply(s).toString(), fallbackSetting, parser, validator, properties);
        this.defaultStringValue = defaultStringValue;
    }

    @Override
    String innerGetRaw(final Settings settings) {
        List<String> array = settings.getAsList(getKey(), null);
        return array == null ? defaultValue.apply(settings) : array.toString();
    }
}
