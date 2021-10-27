package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.util.JacksonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;
import java.util.function.Function;

/**
 * jackson 的 {@link ArrayNode} 可以替代 ListSetting。
 * @param <T>
 */
public class ListSetting<T> extends Setting<List<T>> {

    private final Function<Settings, List<String>> defaultStringValue;

    public ListSetting(ListKey key, Function<Settings, List<String>> defaultStringValue, Setting<List<T>> fallbackSetting, Function<String, List<T>> parser, Validator<List<T>> validator, Property... properties) {
        super(key, s -> JacksonUtil.toJsonString(defaultStringValue.apply(s)), fallbackSetting, parser, validator, properties);
        this.defaultStringValue = defaultStringValue;
    }

    @Override
    String innerGetRaw(final Settings settings) {
        List<String> array = settings.getAsList(getKey(), null);
        return array == null ? defaultValue.apply(settings) : JacksonUtil.toJsonString(array);
    }

    boolean isListSetting() {
        return true;
    }
}
