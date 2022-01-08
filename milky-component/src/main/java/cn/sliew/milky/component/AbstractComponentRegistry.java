package cn.sliew.milky.component;

import cn.sliew.milky.common.constant.AttributeKey;
import cn.sliew.milky.common.constant.Tag;
import cn.sliew.milky.registry.AbstractRegistry;

import java.util.Optional;
import java.util.Set;

/**
 * component base implemention.
 * todo pub/sub component event
 */
public abstract class AbstractComponentRegistry<E extends Component, C>
        extends AbstractRegistry<E, C> implements ComponentRegistry<E, C> {

    @Override
    public Optional<Component> lookup(String name) {
        return (Optional<Component>) entries.find(name);
    }

    @Override
    public Set<String> lookup(Class<? extends Component> componentType) {
        return null;
    }

    @Override
    public Set<String> lookup(Tag tag) {
        return null;
    }

    @Override
    public Set<String> lookup(AttributeKey key) {
        return null;
    }

    @Override
    public boolean exists(String name) {
        return entries.exists(name);
    }

    @Override
    public boolean exists(Component component) {
        return exists(component.getName());
    }

}
