package cn.sliew.milky.property;

/**
 * Allows a setting to declare a dependency on another setting being set.
 * Optionally, a setting can validate the value of the dependent setting.
 */
public interface SettingDependency {

    /**
     * The setting to declare a dependency on.
     *
     * @return the setting
     */
    Setting getSetting();

    /**
     * Validates the dependent setting value.
     *
     * @param key        the key for this setting
     * @param value      the value of this setting
     * @param dependency the value of the dependent setting
     */
    default void validate(String key, Object value, Object dependency) {

    }

}