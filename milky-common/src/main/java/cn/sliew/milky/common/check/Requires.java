package cn.sliew.milky.common.check;

public enum Requires {
    ;

    public static void require(boolean requirement) {
        if (!requirement)
            throw new IllegalArgumentException("requirement failed");
    }

    public static void require(boolean requirement, Object message) {
        if (!requirement)
            throw new IllegalArgumentException("requirement failed: " + message);
    }


}
