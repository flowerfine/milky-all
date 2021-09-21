package cn.sliew.milky.thread;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ThreadPoolConfigEnum {

    SAME("same", ThreadPoolType.DIRECT) {
        private MilkyThreadPoolExecutor executor;
        @Override
        public MilkyThreadPoolExecutor getInstance() {
            if (executor == null) {
                this.executor = ThreadPoolExecutorBuilder.builder()
                        .name(getName())
                        .coreSize(ExecutorUtil.availableProcessors())
                        .maxSize(ExecutorUtil.twiceProcessors())
                        .build();
            }
            return executor;
        }
    },

    ;

    private String name;
    private ThreadPoolType type;

    ThreadPoolConfigEnum(String name, ThreadPoolType type) {
        this.name = name;
        this.type = type;
    }

    private static final Map<String, ThreadPoolConfigEnum> TYPE_MAP;

    static {
        Map<String, ThreadPoolConfigEnum> typeMap = new HashMap<>();
        for (ThreadPoolConfigEnum threadPoolConfigEnum : ThreadPoolConfigEnum.values()) {
            typeMap.put(threadPoolConfigEnum.name, threadPoolConfigEnum);
        }
        TYPE_MAP = Collections.unmodifiableMap(typeMap);
    }

    public static ThreadPoolConfigEnum ofName(String name) {
        ThreadPoolConfigEnum threadPoolConfigEnum = TYPE_MAP.get(name);
        if (threadPoolConfigEnum == null) {
            throw new IllegalArgumentException(String.format("no ThreadPoolConfigEnum for %s", name));
        }
        return threadPoolConfigEnum;
    }

    public abstract MilkyThreadPoolExecutor getInstance();


    public String getName() {
        return name;
    }

    public ThreadPoolType getType() {
        return type;
    }
}
