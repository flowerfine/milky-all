package cn.sliew.milky.common.primitives;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static cn.sliew.milky.common.check.Ensures.checkArgument;

public final class Enums {

    private static final Map<Class<?>, Map<String, ?>> NORMALIZED_ENUMS = new ConcurrentHashMap<>();

    private Enums() {
        throw new AssertionError("No instances intended");
    }

    public static <T1 extends Enum<T1>, T2 extends Enum<T2>> T2 convertTo(final T1 ie, final Class<T2> oe) {
        if (ie == null) {
            return null;
        }

        return Enum.valueOf(oe, ie.name());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Optional<T> toEnum(final String value, final Class<T> enumClass) {
        checkArgument(enumClass.isEnum());

        if (!NORMALIZED_ENUMS.containsKey(enumClass)) {
            final T[] values = enumClass.getEnumConstants();
            final Map<String, T> mappings = new HashMap<>(values.length);
            for (final T t : values) {
                mappings.put(normalizeName(t.name()), t);
            }
            NORMALIZED_ENUMS.put(enumClass, mappings);
        }

        return Optional.ofNullable((T) NORMALIZED_ENUMS.get(enumClass).get(normalizeName(value)));
    }

    private static String normalizeName(final String name) {
        return name.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
    }
}
