package cn.sliew.milky.common.check;

public enum Assumes {
    ;

    public static void assume(boolean assumption) {
        if (!assumption)
            throw new AssertionError("assumption failed");
    }

    public static void assume(boolean assumption, Object message) {
        if (!assumption)
            throw new AssertionError("assumption failed: " + message);
    }
}
