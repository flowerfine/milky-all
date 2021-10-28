package cn.sliew.milky.property;

import java.util.Set;

public interface Setting<T> {

    String getKey();
    Key getRawKey();

    T get(SettingSource settings);
    String getRaw(SettingSource settings);

    boolean exists(SettingSource settings);

    boolean match(String key);
    /**
     * {@link JsonNode#isObject()}
     */
    default boolean isGroupSetting() {
        return false;
    }
    /**
     * {@link JsonNode#isArray()}
     */
    default boolean isListSetting() {
        return false;
    }

    Set<String> getDependencies();
}
