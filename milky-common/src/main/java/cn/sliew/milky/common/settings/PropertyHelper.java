package cn.sliew.milky.common.settings;

public class PropertyHelper {

    private PropertyHelper() {
        throw new IllegalStateException("no instance");
    }

    /**
     * Returns <code>true</code> if this setting is dynamically updateable, otherwise <code>false</code>
     */
    public final boolean isDynamic(Setting setting) {
        return setting.getProperties().contains(Property.Dynamic);
    }

}
