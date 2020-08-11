package cn.sliew.milky.common.settings;

import java.util.Map;

public final class Settings {

    /**
     * The raw settings from the full key to raw string value.
     */
    private final Map<String, Object> settings;

    public Settings(Map<String, Object> settings) {
        this.settings = settings;
    }
}
