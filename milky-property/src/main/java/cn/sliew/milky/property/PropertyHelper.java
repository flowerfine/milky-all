package cn.sliew.milky.property;

import java.util.Arrays;

public class PropertyHelper {

    private PropertyHelper() {
        throw new IllegalStateException("no instance");
    }

    /**
     * Returns <code>true</code> if this setting is dynamically updateable, otherwise <code>false</code>
     */
    public static boolean isDynamic(Setting setting) {
        return setting.getProperties().contains(Property.Dynamic);
    }

    /**
     * Returns <code>true</code> if this setting is sensitive, otherwise <code>false</code>
     */
    public static boolean isSensitive(Setting setting) {
        return setting.getProperties().contains(Property.Sensitive);
    }

    /**
     * Returns <code>true</code> if this setting is sensitive, otherwise <code>false</code>
     */
    public static boolean isSensitive(Property... properties) {
        return properties != null && Arrays.asList(properties).contains(Property.Sensitive);
    }



}
