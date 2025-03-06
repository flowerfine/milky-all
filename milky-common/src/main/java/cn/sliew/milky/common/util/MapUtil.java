package cn.sliew.milky.common.util;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public enum MapUtil {
    ;

    /**
     * jdk8 performance bug, see: https://bugs.openjdk.java.net/browse/JDK-8161372
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
        V v = map.get(key);
        if (Objects.nonNull(v)) {
            return v;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }
}
