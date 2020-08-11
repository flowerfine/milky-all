package cn.sliew.milky.common.settings;

public enum Property {

    /**
     * should be filtered in some api (mask password/credentials)
     */
    Filtered,

    /**
     * iff this setting can be dynamically updateable
     */
    Dynamic,

    /**
     * mark this setting as final, not updateable even when the context is not dynamic
     */
    Final,

    /**
     * mark this setting as deprecated
     */
    Deprecated,
}
