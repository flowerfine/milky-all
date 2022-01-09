package cn.sliew.milky.component;

import cn.sliew.milky.common.constant.AttributeKey;

/**
 * component health probe marker interface.
 */
public interface Probeable {

    AttributeKey<Boolean> LIVENESS = AttributeKey.valueOf("liveness");
    AttributeKey<Boolean> READINESS = AttributeKey.valueOf("readiness");

    /**
     * @return true if component is lively, otherwise false
     */
    boolean isLiveness();

    /**
     * @return true if component is ready, otherwise false
     */
    boolean isReadiness();
}
