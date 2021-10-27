package cn.sliew.milky.property;

public interface Setting {

    /**
     * {@link JsonNode#isObject()}
     * @return
     */
    default boolean isGroupSetting() {
        return false;
    }

    /**
     * {@link JsonNode#isArray()}
     * @return
     */
    default boolean isListSetting() {
        return false;
    }
}
