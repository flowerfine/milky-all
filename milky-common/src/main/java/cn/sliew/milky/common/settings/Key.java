package cn.sliew.milky.common.settings;

/**
 * setting key interface.
 */
public interface Key {

    /**
     * The proper name for the setting. This is the primary mechanism of
     * comparing equality.
     */
    String name();

    /**
     * And explanation of the meaning of the given setting.
     */
    String description();

    /**
     * matches provided name with Key.
     *
     * @param key string key name.
     * @return true matched, false otherwise.
     */
    boolean match(String key);

}