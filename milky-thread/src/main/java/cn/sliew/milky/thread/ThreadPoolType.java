package cn.sliew.milky.thread;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ThreadPoolType {

    DIRECT("direct"),
    FIXED("fixed"),
    SCALING("scaling"),

    ;

    private final String type;

    public String getType() {
        return type;
    }

    ThreadPoolType(String type) {
        this.type = type;
    }

    private static final Map<String, ThreadPoolType> TYPE_MAP;

    static {
        Map<String, ThreadPoolType> typeMap = new HashMap<>();
        for (ThreadPoolType threadPoolType : ThreadPoolType.values()) {
            typeMap.put(threadPoolType.getType(), threadPoolType);
        }
        TYPE_MAP = Collections.unmodifiableMap(typeMap);
    }

    public static ThreadPoolType fromType(String type) {
        ThreadPoolType threadPoolType = TYPE_MAP.get(type);
        if (threadPoolType == null) {
            throw new IllegalArgumentException(String.format("no ThreadPoolType for %s", type));
        }
        return threadPoolType;
    }
}
