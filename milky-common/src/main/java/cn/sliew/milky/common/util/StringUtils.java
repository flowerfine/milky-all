package cn.sliew.milky.common.util;

import java.util.Arrays;

import static cn.sliew.milky.common.util.ThrowableUtil.rethrowIfUnrecoverable;

public final class StringUtils {

    private StringUtils() {
        throw new IllegalStateException("can't do this!");
    }

    /**
     * Determine if the supplied {@link String} is <em>blank</em> (i.e.,
     * {@code null} or consisting only of whitespace characters).
     *
     * @param str the string to check; may be {@code null}
     * @return {@code true} if the string is blank
     * @see #isNotBlank(String)
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().isEmpty());
    }

    /**
     * Determine if the supplied {@link String} is not {@linkplain #isBlank
     * blank}.
     *
     * @param str the string to check; may be {@code null}
     * @return {@code true} if the string is not blank
     * @see #isBlank(String)
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Test whether the given string matches the given substring
     * at the given index.
     *
     * @param str       the original string (or StringBuilder)
     * @param index     the index in the original string to start matching against
     * @param substring the substring to match at the given index
     */
    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static String camelToSplitName(String camelName, String split) {
        if (isBlank(camelName)) {
            return camelName;
        }
        StringBuilder buf = null;
        for (int i = 0; i < camelName.length(); i++) {
            char ch = camelName.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                if (buf == null) {
                    buf = new StringBuilder();
                    if (i > 0) {
                        buf.append(camelName, 0, i);
                    }
                }
                if (i > 0) {
                    buf.append(split);
                }
                buf.append(Character.toLowerCase(ch));
            } else if (buf != null) {
                buf.append(ch);
            }
        }
        return buf == null ? camelName : buf.toString();
    }


    /**
     * Convert the supplied {@code Object} to a {@code String} using the
     * following algorithm.
     *
     * <ul>
     * <li>If the supplied object is {@code null}, this method returns {@code "null"}.</li>
     * <li>If the supplied object is a primitive array, the appropriate
     * {@code Arrays#toString(...)} variant will be used to convert it to a String.</li>
     * <li>If the supplied object is an object array, {@code Arrays#deepToString(Object[])}
     * will be used to convert it to a String.</li>
     * <li>Otherwise, {@code toString()} will be invoked on the object. If the
     * result is non-null, that result will be returned. If the result is
     * {@code null}, {@code "null"} will be returned.</li>
     * <li>If any of the above results in an exception, this method delegates to
     * {@link #defaultToString(Object)}</li>
     * </ul>
     *
     * @param obj the object to convert to a String; may be {@code null}
     * @return a String representation of the supplied object; never {@code null}
     * @see Arrays#deepToString(Object[])
     * @see ClassUtil#nullSafeToString(Class...)
     */
    public static String nullSafeToString(Object obj) {
        if (obj == null) {
            return "null";
        }

        try {
            if (obj.getClass().isArray()) {
                if (obj.getClass().getComponentType().isPrimitive()) {
                    if (obj instanceof boolean[]) {
                        return Arrays.toString((boolean[]) obj);
                    }
                    if (obj instanceof char[]) {
                        return Arrays.toString((char[]) obj);
                    }
                    if (obj instanceof short[]) {
                        return Arrays.toString((short[]) obj);
                    }
                    if (obj instanceof byte[]) {
                        return Arrays.toString((byte[]) obj);
                    }
                    if (obj instanceof int[]) {
                        return Arrays.toString((int[]) obj);
                    }
                    if (obj instanceof long[]) {
                        return Arrays.toString((long[]) obj);
                    }
                    if (obj instanceof float[]) {
                        return Arrays.toString((float[]) obj);
                    }
                    if (obj instanceof double[]) {
                        return Arrays.toString((double[]) obj);
                    }
                }
                return Arrays.deepToString((Object[]) obj);
            }

            // else
            String result = obj.toString();
            return result != null ? result : "null";
        } catch (Throwable throwable) {
            rethrowIfUnrecoverable(throwable);
            return defaultToString(obj);
        }
    }

    /**
     * Convert the supplied {@code Object} to a <em>default</em> {@code String}
     * representation using the following algorithm.
     *
     * <ul>
     * <li>If the supplied object is {@code null}, this method returns {@code "null"}.</li>
     * <li>Otherwise, the String returned by this method will be generated analogous
     * to the default implementation of {@link Object#toString()} by using the supplied
     * object's class name and hash code as follows:
     * {@code obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj))}</li>
     * </ul>
     *
     * @param obj the object to convert to a String; may be {@code null}
     * @return the default String representation of the supplied object; never {@code null}
     * @see #nullSafeToString(Object)
     * @see ClassUtil#nullSafeToString(Class...)
     */
    public static String defaultToString(Object obj) {
        if (obj == null) {
            return "null";
        }

        return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }
}
