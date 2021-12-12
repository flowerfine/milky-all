package cn.sliew.milky.property;

public enum Property {

    /**
     * mark this setting as deprecated.
     */
    Deprecated,

    /**
     * mark this setting as final, not updateable even when the context is not dynamic.
     */
    Final,

    /**
     * if this setting can be dynamically updateable.
     */
    Dynamic,

    /**
     * Determines whether the property is required.
     */
    Required,

    /**
     * indicates that the value for this property should be considered sensitive
     * and protected whenever stored or represented
     */
    Sensitive,
}
