package cn.sliew.milky.common.check;

import java.util.function.Supplier;

public enum Requires {
    ;

    public static void require(boolean requirement) {
        if (!requirement)
            throw new IllegalArgumentException("requirement failed");
    }

    public static void require(boolean requirement, Supplier<Object> errorMessage) {
        if (!requirement)
            throw new IllegalArgumentException("requirement failed: " + errorMessage.get());
    }


}
