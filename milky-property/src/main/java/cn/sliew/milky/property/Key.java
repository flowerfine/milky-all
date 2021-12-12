package cn.sliew.milky.property;

/**
 * setting key interface.
 */
public interface Key {

    /**
     * matches provided name with Key.
     *
     * @param key string key name.
     * @return true matched, false otherwise.
     */
    boolean match(String key);

    /**
     * The proper display name for the setting. This is the primary mechanism of
     * comparing equality.
     */
    String getDisplayName();

    /**
     * And explanation of the meaning of the given setting.
     */
    String getDescription();

}