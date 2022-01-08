package cn.sliew.milky.component;

import cn.sliew.milky.common.constant.AttributeKey;
import cn.sliew.milky.common.constant.Tag;

import java.util.Set;

/**
 * component lookup service.
 */
public interface ComponentLookupService {

    /**
     * @param name component unique name
     * @return Component indentified by unique name
     */
    Component lookup(String name);

    /**
     * @param componentType component class
     * @return all component names by component type
     */
    Set<String> lookup(Class<? extends Component> componentType);

    /**
     * @param tag the tag
     * @return all component by specified tag
     */
    Set<String> lookup(Tag tag);

    /**
     * @param key the attribute
     * @return all component by specified attribute
     */
    Set<String> lookup(AttributeKey key);

    /**
     * @param name component unique name
     * @return true if component indentified by unique name exists, otherwise false
     */
    boolean exists(String name);

    /**
     * @param component
     * @return true if component exists, otherwise false
     */
    boolean exists(Component component);
}
