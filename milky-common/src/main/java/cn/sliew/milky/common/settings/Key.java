package cn.sliew.milky.common.settings;

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

}