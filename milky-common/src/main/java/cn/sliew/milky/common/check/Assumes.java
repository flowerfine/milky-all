package cn.sliew.milky.common.check;

import java.util.function.Supplier;

public enum Assumes {
    ;

    public static void assume(boolean assumption) {
        if (!assumption)
            throw new AssertionError("assumption failed");
    }

    public static void assume(boolean assumption, Supplier<Object> errorMessage) {
        if (!assumption)
            throw new AssertionError("assumption failed: " + errorMessage.get());
    }
}
