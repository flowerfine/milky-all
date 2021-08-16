package cn.sliew.milky.common.component;

import cn.sliew.milky.common.constant.AttributeMap;
import cn.sliew.milky.common.constant.TagSet;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface Componenet extends AttributeMap, TagSet, Serializable {

    /**
     * jdbc mapper name
     */
    String name();

    /**
     * namespace
     */
    String namespace();

    /**
     * group
     */
    String group();

    /**
     * custom key/value attributes.
     * todo AttributeMap get all attributes
     */
    Map<String, String> attributes();
}
