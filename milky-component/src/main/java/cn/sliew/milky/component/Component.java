package cn.sliew.milky.component;

import cn.sliew.milky.common.constant.AttributeMap;
import cn.sliew.milky.common.constant.TagSet;

import java.io.Serializable;

/**
 * component marker interface, which support custom tags and attrubutes.
 */
public interface Component extends TagSet, AttributeMap,
        Versionable, Tenantable, Probeable, Managable, Serializable {

    /**
     * @return component display name
     */
    String getName();

    /**
     * @return component unqiue identifier
     */
    String getIdentifier();
}
