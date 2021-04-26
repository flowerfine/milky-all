package cn.sliew.milky.common.primitives;

import static cn.sliew.milky.common.primitives.Strings.hasText;

public final class Doubles {

    private Doubles() {
        throw new AssertionError("No instances intended");
    }

    public static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Failed to parse double value [%s]", value));
        }
    }

    public static double parseDouble(String value, double defaultValue) {
        if (hasText(value)) {
            return parseDouble(value);
        }
        return defaultValue;
    }

    public static Double parseDouble(String value, Double defaultValue) {
        if (hasText(value)) {
            return parseDouble(value);
        }
        return defaultValue;
    }

}
