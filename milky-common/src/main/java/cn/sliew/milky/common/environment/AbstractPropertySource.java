package cn.sliew.milky.common.environment;

import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;

import java.util.Objects;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.notBlank;

public abstract class AbstractPropertySource<T> implements PropertySource<T> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final String name;

    protected final T source;

    public AbstractPropertySource(String name, T source) {
        this.name = notBlank(name, () -> "Property source name can't be blank");
        this.source = checkNotNull(source, () -> "Property source object must not be null");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T getSource() {
        return this.source;
    }

    @Override
    public boolean containsProperty(String property) {
        return getProperty(property).isPresent();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractPropertySource<?> that = (AbstractPropertySource<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        if (log.isDebugEnabled()) {
            return getClass().getSimpleName() + "@" + System.identityHashCode(this) +
                    " {name='" + getName() + "', properties=" + getSource() + "}";
        }
        else {
            return getClass().getSimpleName() + " {name='" + getName() + "'}";
        }
    }
}
