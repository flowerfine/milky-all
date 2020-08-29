package cn.sliew.milky.common;

public enum Asserts {
    ;

    public static void asserts(boolean assertion) {
        if (!assertion)
            throw new java.lang.AssertionError("assertion failed");
    }

    public static void asserts(boolean assertion, Object message) {
        if (!assertion)
            throw new java.lang.AssertionError("assertion failed: " + message);
    }
}
