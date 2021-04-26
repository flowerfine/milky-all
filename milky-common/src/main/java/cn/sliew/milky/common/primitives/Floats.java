package cn.sliew.milky.common.primitives;

import static cn.sliew.milky.common.primitives.Strings.hasText;

public final class Floats {

    private Floats() {
        throw new AssertionError("No instances intended");
    }

    public static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Failed to parse float value [%s]", value));
        }
    }

    public static float parseFloat(String value, float defaultValue) {
        if (hasText(value)) {
            return parseFloat(value);
        }
        return defaultValue;
    }

    public static Float parseFloat(String value, Float defaultValue) {
        if (hasText(value)) {
            return parseFloat(value);
        }
        return defaultValue;
    }

}
