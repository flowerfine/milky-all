package cn.sliew.milky.component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * component base implemention.
 * todo tags and attrubutes lookup
 * todo pub/sub component event
 */
public abstract class AbstractComponentLookupService implements ComponentLookupService {

    private ConcurrentMap<String, Component> componentRegistry = new ConcurrentHashMap<>();

    private Set<Component> components = new ConcurrentSkipListSet<>();

    @Override
    public Component lookup(String name) {
        return componentRegistry.get(name);
    }

    @Override
    public Set<String> lookup(Class<? extends Component> componentType) {
        return null;
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
