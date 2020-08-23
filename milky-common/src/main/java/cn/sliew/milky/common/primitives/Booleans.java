package cn.sliew.milky.common.primitives;

/**
 * Static utility methods pertaining to {@code boolean} primitives.
 */
public final class Booleans {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private Booleans() {
        throw new AssertionError("No instances intended");
    }

    /**
     * returns true iff the sequence of chars is one of "true","false".
     *
     * @param text   sequence to check
     * @param offset offset to start
     * @param length length to check
     */
    public static boolean isBoolean(char[] text, int offset, int length) {
        if (text == null || length == 0) {
            return false;
        }
        return isBoolean(new String(text, offset, length));
    }

    public static boolean isBoolean(String value) {
        return isFalse(value) || isTrue(value);
    }

    /**
     * @return {@code true} iff the value is "false", otherwise {@code false}.
     */
    public static boolean isFalse(String value) {
        return FALSE.equals(value);
    }

    /**
     * @return {@code true} iff the value is "true", otherwise {@code false}.
     */
    public static boolean isTrue(String value) {
        return TRUE.equals(value);
    }

    /**
     * Parses a char[] representation of a boolean value to <code>boolean</code>.
     *
     * @return <code>true</code> iff the sequence of chars is "true", <code>false</code> iff the sequence of chars is "false" or the
     * provided default value iff either text is <code>null</code> or length == 0.
     * @throws IllegalArgumentException if the string cannot be parsed to boolean.
     */
    public static boolean parseBoolean(char[] text, int offset, int length, boolean defaultValue) {
        if (text == null || length == 0) {
            return defaultValue;
        } else {
            return parseBoolean(new String(text, offset, length));
        }
    }

    /**
     * Parses a string representation of a boolean value to <code>boolean</code>.
     *
     * @return <code>true</code> iff the provided value is "true". <code>false</code> iff the provided value is "false".
     * @throws IllegalArgumentException if the string cannot be parsed to boolean.
     */
    public static boolean parseBoolean(String value) {
        if (isFalse(value)) {
            return false;
        }
        if (isTrue(value)) {
            return true;
        }
        throw new IllegalArgumentException("Failed to parse value [" + value + "] as only [true] or [false] are allowed.");
    }

    /**
     * @param value        text to parse.
     * @param defaultValue The default value to return if the provided value is <code>null</code>.
     * @return see {@link #parseBoolean(String)}
     */
    public static boolean parseBoolean(String value, boolean defaultValue) {
        if (hasText(value)) {
            return parseBoolean(value);
        }
        return defaultValue;
    }

    public static Boolean parseBoolean(String value, Boolean defaultValue) {
        if (hasText(value)) {
            return parseBoolean(value);
        }
        return defaultValue;
    }

    private static boolean hasText(CharSequence str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
