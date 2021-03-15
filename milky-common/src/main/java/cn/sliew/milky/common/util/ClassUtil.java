package cn.sliew.milky.common.util;

import java.util.function.Function;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public final class ClassUtil {

    private ClassUtil() {
        throw new IllegalStateException("can't do this!");
    }

    /**
     * Get the fully qualified name of the supplied class.
     *
     * <p>This is a null-safe variant of {@link Class#getName()}.
     *
     * @param clazz the class whose name should be retrieved, potentially {@code null}
     * @return the fully qualified class name or {@code "null"} if the supplied
     * class reference is {@code null}
     * @see #nullSafeToString(Class...)
     * @see StringUtils#nullSafeToString(Object)
     */
    public static String nullSafeToString(Class<?> clazz) {
        return clazz == null ? "null" : clazz.getName();
    }

    /**
     * Generate a comma-separated list of fully qualified class names for the
     * supplied classes.
     *
     * @param classes the classes whose names should be included in the generated string
     * @return a comma-separated list of fully qualified class names, or an empty
     * string if the supplied class array is {@code null} or empty
     * @see #nullSafeToString(Function, Class...)
     * @see StringUtils#nullSafeToString(Object)
     */
    public static String nullSafeToString(Class<?>... classes) {
        return nullSafeToString(Class::getName, classes);
    }

    /**
     * Generate a comma-separated list of mapped values for the supplied classes.
     *
     * <p>The values are generated by the supplied {@code mapper}
     * (e.g., {@code Class::getName}, {@code Class::getSimpleName}, etc.), unless
     * a class reference is {@code null} in which case it will be mapped to {@code "null"}.
     *
     * @param mapper  the mapper to use; never {@code null}
     * @param classes the classes to map
     * @return a comma-separated list of mapped values, or an empty string if
     * the supplied class array is {@code null} or empty
     * @see #nullSafeToString(Class...)
     * @see StringUtils#nullSafeToString(Object)
     */
    public static String nullSafeToString(Function<? super Class<?>, ? extends String> mapper, Class<?>... classes) {
        checkNotNull(mapper, "Mapping function must not be null");

        if (classes == null || classes.length == 0) {
            return "";
        }
        return stream(classes).map(clazz -> clazz == null ? "null" : mapper.apply(clazz)).collect(joining(", "));
    }

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>
     * Call this method if you intend to use the thread context ClassLoader in a
     * scenario where you absolutely need a non-null ClassLoader reference: for
     * example, for class path resource loading (but not necessarily for
     * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
     * reference as well).
     *
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassUtil.class);
    }

    /**
     * get class loader
     *
     * @param clazz
     * @return class loader
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = clazz.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }

        return cl;
    }
}
