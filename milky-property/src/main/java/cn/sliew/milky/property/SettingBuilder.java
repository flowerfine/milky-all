package cn.sliew.milky.property;

import java.util.function.Function;

/**
 * {@link Setting} Builder.
 */
class SettingBuilder<T> {

    private Key key;
    private Function<Settings, String> defaultValue;
    private Setting<T> fallbackSetting;
    private Function<String, T> parser;
    private Validator<T> validator;

    private Property[] properties;

    SettingBuilder() {

    }

    public SettingBuilder<T> key(Key key) {
        this.key = key;
        return this;
    }

    public SettingBuilder<T> defaultValue(Function<Settings, String> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public SettingBuilder<T> fallback(Setting<T> fallbackSetting) {
        this.fallbackSetting = fallbackSetting;
        return this;
    }

    public SettingBuilder<T> parser(Function<String, T> parser) {
        this.parser = parser;
        return this;
    }

    public SettingBuilder<T> validator(Validator<T> validator) {
        this.validator = validator;
        return this;
    }

    public SettingBuilder<T> defaultValue(Property... properties) {
        this.properties = properties;
        return this;
    }

    public Setting<T> build() {
        return new Setting<T>(key, defaultValue, fallbackSetting, parser, validator, properties);
    }
    
}
