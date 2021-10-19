package cn.sliew.milky.common.settings;

public class SettingHelper {

    private SettingHelper() {
        throw new IllegalStateException("no instance");
    }

    public static Setting<Float> floatSetting(String key, float defaultValue, Property... properties) {
        return new Setting<>(key, (s) -> Float.toString(defaultValue), Float::parseFloat, properties);
    }

    public static Setting<Float> floatSetting(String key, float defaultValue, float minValue, Property... properties) {
        return new Setting<>(key, (s) -> Float.toString(defaultValue), (s) -> {
            float value = Float.parseFloat(s);
            if (value < minValue) {
                String err = "Failed to parse value" +
                        (isFiltered(properties) ? "" : " [" + s + "]") + " for setting [" + key + "] must be >= " + minValue;
                throw new IllegalArgumentException(err);
            }
            return value;
        }, properties);
    }
}
