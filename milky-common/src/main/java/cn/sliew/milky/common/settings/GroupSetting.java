package cn.sliew.milky.common.settings;

import java.util.function.Function;

public class GroupSetting extends Setting<Settings> {

    public GroupSetting(Key key, Function<Settings, String> defaultValue, Setting<Settings> fallbackSetting, Function<String, Settings> parser, Validator<Settings> validator) {
        super(key, defaultValue, fallbackSetting, parser, validator);
    }

}
