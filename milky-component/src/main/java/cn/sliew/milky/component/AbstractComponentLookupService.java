package cn.sliew.milky.component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractComponentLookupService implements ComponentLookupService {

    private ConcurrentMap<String, Component> componentRegistry = new ConcurrentHashMap<>();

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
