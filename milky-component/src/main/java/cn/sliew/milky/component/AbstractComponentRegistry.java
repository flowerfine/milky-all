package cn.sliew.milky.component;

import cn.sliew.milky.common.constant.AttributeKey;
import cn.sliew.milky.common.constant.Tag;
import cn.sliew.milky.registry.AbstractRegistry;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * component base implemention.
 */
public abstract class AbstractComponentRegistry<E extends Component, C>
        extends AbstractRegistry<E, C> implements ComponentRegistry<E, C> {

    @Override
    public Optional<Component> lookup(String name) {
        return (Optional<Component>) entries.find(name);
    }

    @Override
    public Set<String> lookup(Class<? extends Component> componentType) {
        return entries.values().stream()
                .filter(component -> component.getClass().equals(componentType))
                .map(Component::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> lookup(Tag tag) {
        return entries.values().stream()
                .filter(component -> component.hasTag(tag))
                .map(Component::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> lookup(AttributeKey key) {
        return entries.values().stream()
                .filter(component -> component.hasAttr(key))
                .map(Component::getName)
                .collect(Collectors.toSet());
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
