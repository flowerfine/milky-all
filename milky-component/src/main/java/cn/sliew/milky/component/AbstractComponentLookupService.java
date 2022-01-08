package cn.sliew.milky.component;

import cn.sliew.milky.common.constant.AttributeKey;
import cn.sliew.milky.common.constant.Tag;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * component base implemention.
 * todo pub/sub component event
 */
public abstract class AbstractComponentLookupService implements ComponentLookupService {

    private ConcurrentMap<String, Component> componentRegistry = new ConcurrentHashMap<>();

    private Set<Component> components = new ConcurrentSkipListSet<>();

    @Override
    public Optional<Component> lookup(String name) {
        return Optional.ofNullable(componentRegistry.get(name));
    }

    @Override
    public Set<String> lookup(Class<? extends Component> componentType) {
        return components.stream()
                .filter(component -> component.getClass().equals(componentType))
                .map(Component::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> lookup(Tag tag) {
        return components.stream().filter(component -> component.hasTag(tag))
                .map(Component::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> lookup(AttributeKey key) {
        return components.stream().filter(component -> component.hasAttr(key))
                .map(Component::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean exists(String name) {
        return componentRegistry.containsKey(name);
    }

    @Override
    public boolean exists(Component component) {
        return componentRegistry.containsKey(component.getName());
    }
}
