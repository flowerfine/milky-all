package cn.sliew.milky.component;

/**
 * manage marker interface.
 */
public interface Managable {

    /**
     * @return organization belonged by componet
     */
    String getOrganization();

    /**
     * @return architecture belonged by componet
     */
    String getArchitecture();

    /**
     * @return module belonged by componet
     */
    String getModule();

    /**
     * @return owner belonged by componet
     */
    String getOwner();

}
