package cn.sliew.milky.common.primitives;

import static cn.sliew.milky.common.primitives.Strings.hasText;

public final class Longs {

    private Longs() {
        throw new AssertionError("No instances intended");
    }

    public static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Failed to parse long value [%s]", value));
        }
    }

    public static long parseLong(String value, long defaultValue) {
        if (hasText(value)) {
            return parseLong(value);
        }
        return defaultValue;
    }

    public static Long parseLong(String value, Long defaultValue) {
        if (hasText(value)) {
            return parseLong(value);
        }
        return defaultValue;
    }

}
