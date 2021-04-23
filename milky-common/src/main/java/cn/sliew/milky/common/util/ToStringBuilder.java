package cn.sliew.milky.common.util;


import java.util.ArrayList;
import java.util.List;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.notBlank;
import static java.lang.String.join;

/**
 * Simple builder for generating strings in custom implementations of
 * {@link Object#toString toString()}.
 *
 * <h3>DISCLAIMER</h3>
 *
 * <p>These utilities are intended solely for usage within the JUnit framework
 * itself. <strong>Any usage by external parties is not supported.</strong>
 * Use at your own risk!
 */
public class ToStringBuilder {

    private final String typeName;

    private final List<String> values = new ArrayList<>();

    public ToStringBuilder(Object obj) {
        this(checkNotNull(obj, () -> "Object must not be null").getClass().getSimpleName());
    }

    public ToStringBuilder(Class<?> type) {
        this(checkNotNull(type, () -> "Class must not be null").getSimpleName());
    }

    public ToStringBuilder(String typeName) {
        this.typeName = checkNotNull(typeName, () -> "Type name must not be null");
    }

    public ToStringBuilder append(String name, Object value) {
        notBlank(name, () -> "Name must not be null or blank");
        this.values.add(name + " = " + toString(value));
        return this;
    }

    private String toString(Object obj) {
        return (obj instanceof CharSequence) ? ("'" + obj + "'") : StringUtils.nullSafeToString(obj);
    }

    @Override
    public String toString() {
        return this.typeName + " [" + join(", ", this.values) + "]";
    }

}
