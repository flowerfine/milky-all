package cn.sliew.milky.common.settings;

import cn.sliew.milky.log.Logger;

import java.io.IOException;
import java.util.function.Consumer;

public class GroupSetting extends Setting<Settings> {

    private final GroupKey key;
    private final Consumer<Settings> validator;

    public GroupSetting(GroupKey key, Consumer<Settings> validator) {
        super(key, (s) -> "", null, (s) -> null, v -> {});
        this.key = key;
        this.validator = validator;
    }

    @Override
    boolean isGroupSetting() {
        return true;
    }

    /**
     * todo 将 map 转变成 json
     */
    @Override
    public String innerGetRaw(final Settings settings) {
        Settings subSettings = get(settings);
        return subSettings.toDelimitedString('=');
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
            if (settingsKey.startsWith(key.name())) {
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
