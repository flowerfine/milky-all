package cn.sliew.milky.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public final class ReflectUtil {

    private ReflectUtil() {
        throw new IllegalStateException("no instance");
    }

    /**
     * Find the {@link Set} of {@link ParameterizedType}
     *
     * @param clazz the source {@link Class class}
     * @return non-null read-only {@link Set}
     */
    public static Set<ParameterizedType> findParameterizedTypes(Class<?> clazz) {
        // Add Generic Interfaces
        List<Type> genericTypes = new LinkedList<>(asList(clazz.getGenericInterfaces()));
        // Add Generic Super Class
        genericTypes.add(clazz.getGenericSuperclass());

        Set<ParameterizedType> parameterizedTypes = genericTypes.stream()
                .filter(type -> type instanceof ParameterizedType)// filter ParameterizedType
                .map(type -> ParameterizedType.class.cast(type))  // cast to ParameterizedType
                .collect(Collectors.toSet());

        if (parameterizedTypes.isEmpty()) { // If not found, try to search super types recursively
            genericTypes.stream()
                    .filter(type -> type instanceof Class)
                    .map(type -> Class.class.cast(type))
                    .forEach(superClass -> {
                        parameterizedTypes.addAll(findParameterizedTypes(superClass));
                    });
        }

        return unmodifiableSet(parameterizedTypes);                     // build as a Set
    }

}
