package cn.sliew.milky.component;

import cn.sliew.milky.registry.Registry;

public interface ComponentRegistry<E extends Component, C>
        extends ComponentLookupService, Registry<E, C> {

}
