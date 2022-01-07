package cn.sliew.milky.component;

/**
 * tenant marker interface.
 * eg. milky/middleware/scheduler/dev
 */
public interface Tenantable {

    /**
     * @return tenant namespace scope
     */
    String getNamespace();

    /**
     * @return tenant applicaton scope
     */
    String getApplication();

    /**
     * @return tenant environment scope
     */
    String getEnvironment();

}
