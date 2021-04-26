package cn.sliew.milky.common.primitives;

import static cn.sliew.milky.common.primitives.Strings.hasText;

public final class Integers {

    private Integers() {
        throw new AssertionError("No instances intended");
    }

    public static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Failed to parse int value [%s]", value));
        }
    }

    public static int parseInteger(String value, int defaultValue) {
        if (hasText(value)) {
            return parseInteger(value);
        }
        return defaultValue;
    }

    public static Integer parseInteger(String value, Integer defaultValue) {
        if (hasText(value)) {
            return parseInteger(value);
        }
        return defaultValue;
    }

}
