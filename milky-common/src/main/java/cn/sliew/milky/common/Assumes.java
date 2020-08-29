package cn.sliew.milky.common;

public enum Assumes {
    ;

    public static void assume(boolean assumption) {
        if (!assumption)
            throw new java.lang.AssertionError("assumption failed");
    }

    public static void assume(boolean assumption, Object message) {
        if (!assumption)
            throw new java.lang.AssertionError("assumption failed: " + message);
    }


}
