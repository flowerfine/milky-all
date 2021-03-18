package cn.sliew.milky.serialize.support.support;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provide a unified serialization registry, this class used for {@code fst}
 * and {@code kryo}, it will register some classes at startup time (for example {@link AbstractKryoFactory#create})
 */
public abstract class SerializableClassRegistry {

    private static final Map<Class<?>, Object> REGISTRATIONS = new LinkedHashMap<>();

    /**
     * only supposed to be called at startup time
     *
     * @param clazz object type
     */
    public static void registerClass(Class<?> clazz) {
        registerClass(clazz, null);
    }

    /**
     * only supposed to be called at startup time
     *
     * @param clazz      object type
     * @param serializer object serializer
     */
    public static void registerClass(Class<?> clazz, Object serializer) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class registered to kryo cannot be null!");
        }
        REGISTRATIONS.put(clazz, serializer);
    }

    /**
     * get registered classes
     *
     * @return class serializer
     */
    public static Map<Class<?>, Object> getRegisteredClasses() {
        return REGISTRATIONS;
    }
}
