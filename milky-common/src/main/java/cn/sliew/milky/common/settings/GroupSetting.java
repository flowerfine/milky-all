package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.util.JacksonUtil;
import cn.sliew.milky.log.Logger;

import java.util.function.Consumer;

public class GroupSetting extends Setting<Settings> {

    private final String key;
    private final Consumer<Settings> validator;

    public GroupSetting(String key, Consumer<Settings> validator, Property... properties) {
        super(new GroupKey(key), (s) -> "", null, (s) -> null, v -> {}, properties);
        this.key = key;
        this.validator = validator;
    }

    @Override
    boolean isGroupSetting() {
        return true;
    }

    @Override
    public String innerGetRaw(final Settings settings) {
        Settings subSettings = get(settings);
        return JacksonUtil.toJsonString(subSettings.settings);
    }

    @Override
    public Settings get(Settings settings) {
        Settings byPrefix = settings.getByPrefix(getKey());
        validator.accept(byPrefix);
        return byPrefix;
    }

    @Override
    public boolean exists(Settings settings) {
        for (String settingsKey : settings.keySet()) {
            if (settingsKey.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SettingUpdater<Settings> newUpdater(Consumer<Settings> updater, Consumer<Settings> validator, Logger logger) {
        return new Updater(updater, validator, logger);
    }

    private final class Updater implements SettingUpdater<Settings> {

        private final Consumer<Settings> updater;
        private final Consumer<Settings> validator;
        private final Logger log;

        public Updater(Consumer<Settings> updater, Consumer<Settings> validator, Logger log) {
            this.updater = updater;
            this.validator = validator;
            this.log = log;
        }


        @Override
        public boolean hasChanged(Settings current, Settings previous) {
            Settings currentSettings = get(current);
            Settings previousSettings = get(previous);
            return currentSettings.equals(previousSettings) == false;
        }

        @Override
        public Settings getValue(Settings current, Settings previous) {
            Settings newValue = get(current);
            Settings value = get(previous);
            try {
                validator.accept(newValue);
            } catch (Exception | AssertionError e) {
                throw new IllegalArgumentException(
                        String.format("illegal value can't update [%s] from [%s] to [%s]", getRawKey(), value, newValue),
                        e);
            }
            return newValue;
        }

        @Override
        public void apply(Settings value, Settings current, Settings previous) {
            log.info("updating [{}] from [{}] to [{}]", getRawKey(), getRaw(previous), getRaw(current));
            updater.accept(value);
        }

        @Override
        public String toString() {
            return "Updater for: " + GroupSetting.this.toString();
        }
    }
}
