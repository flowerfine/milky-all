package cn.sliew.milky.component;

/**
 * component health probe marker interface.
 */
public interface Probeable {

    /**
     * @return true if component is lively, otherwise false
     */
    boolean isLiveness();

    /**
     * @return true if component is ready, otherwise false
     */
    boolean isReadiness();
}
