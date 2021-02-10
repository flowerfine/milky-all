package cn.sliew.milky.common.check;

import cn.sliew.milky.common.util.StringUtils;
import com.google.common.base.Verify;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Static convenience methods that help a method or constructor check whether it was invoked
 * correctly.
 *
 * <h3>Other types of preconditions</h3>
 *
 * <p>Not every type of precondition failure is supported by these methods. Continue to throw
 * standard JDK exceptions such as {@link java.util.NoSuchElementException} or {@link
 * UnsupportedOperationException} in the situations they are intended for.
 * <p>
 * href="https://github.com/google/guava/wiki/ConditionalFailuresExplained">Conditional failures explained</a>
 *
 * <h3>{@code java.util.Objects.requireNonNull()}</h3>
 *
 * <p>Projects which use {@code com.google.common} should generally avoid the use of {@link
 * java.util.Objects#requireNonNull(Object)}. Instead, use whichever of {@link
 * #checkNotNull(Object)} or {@link Verify#verifyNotNull(Object)} is appropriate to the situation.
 * (The same goes for the message-accepting overloads.)
 */
public enum Ensures {

    ;

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, @Nullable Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    public static <T extends Object> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T extends Object> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Assert that the supplied array is neither {@code null} nor <em>empty</em>.
     *
     * <p><strong>WARNING</strong>: this method does NOT check if the supplied
     * array contains any {@code null} elements.
     *
     * @param array   the array to check
     * @param message precondition violation message
     * @return the supplied array as a convenience
     * @throws IllegalStateException if the supplied array is
     *                               {@code null} or <em>empty</em>
     * @see #condition(boolean, String)
     */
    public static <T> T[] notEmpty(T[] array, String message) {
        condition(array != null && array.length > 0, message);
        return array;
    }

    /**
     * Assert that the supplied array is neither {@code null} nor <em>empty</em>.
     *
     * <p><strong>WARNING</strong>: this method does NOT check if the supplied
     * array contains any {@code null} elements.
     *
     * @param array           the array to check
     * @param messageSupplier precondition violation message supplier
     * @return the supplied array as a convenience
     * @throws IllegalStateException if the supplied array is
     *                               {@code null} or <em>empty</em>
     * @see #condition(boolean, String)
     */
    public static <T> T[] notEmpty(T[] array, Supplier<String> messageSupplier) {
        condition(array != null && array.length > 0, messageSupplier);
        return array;
    }

    /**
     * Assert that the supplied {@link Collection} is neither {@code null} nor empty.
     *
     * <p><strong>WARNING</strong>: this method does NOT check if the supplied
     * collection contains any {@code null} elements.
     *
     * @param collection the collection to check
     * @param message    precondition violation message
     * @return the supplied collection as a convenience
     * @throws IllegalStateException if the supplied collection is {@code null} or empty
     * @see #condition(boolean, String)
     */
    public static <T extends Collection<?>> T notEmpty(T collection, String message) {

        condition(collection != null && !collection.isEmpty(), message);
        return collection;
    }

    /**
     * Assert that the supplied {@link Collection} is neither {@code null} nor empty.
     *
     * <p><strong>WARNING</strong>: this method does NOT check if the supplied
     * collection contains any {@code null} elements.
     *
     * @param collection      the collection to check
     * @param messageSupplier precondition violation message supplier
     * @return the supplied collection as a convenience
     * @throws IllegalStateException if the supplied collection is {@code null} or empty
     * @see #condition(boolean, String)
     */
    public static <T extends Collection<?>> T notEmpty(T collection, Supplier<String> messageSupplier) {

        condition(collection != null && !collection.isEmpty(), messageSupplier);
        return collection;
    }

    /**
     * Assert that the supplied {@link String} is not blank.
     *
     * <p>A {@code String} is <em>blank</em> if it is {@code null} or consists
     * only of whitespace characters.
     *
     * @param str     the string to check
     * @param message precondition violation message
     * @return the supplied string as a convenience
     * @throws IllegalStateException if the supplied string is blank
     * @see #notBlank(String, Supplier)
     */
    public static String notBlank(String str, String message) {
        condition(StringUtils.isNotBlank(str), message);
        return str;
    }

    /**
     * Assert that the supplied {@link String} is not blank.
     *
     * <p>A {@code String} is <em>blank</em> if it is {@code null} or consists
     * only of whitespace characters.
     *
     * @param str             the string to check
     * @param messageSupplier precondition violation message supplier
     * @return the supplied string as a convenience
     * @throws IllegalStateException if the supplied string is blank
     * @see StringUtils#isNotBlank(String)
     * @see #condition(boolean, Supplier)
     */
    public static String notBlank(String str, Supplier<String> messageSupplier) {
        condition(StringUtils.isNotBlank(str), messageSupplier);
        return str;
    }

    /**
     * Assert that the supplied {@code predicate} is {@code true}.
     *
     * @param predicate the predicate to check
     * @param message   precondition violation message
     * @throws IllegalStateException if the predicate is {@code false}
     * @see #condition(boolean, Supplier)
     */
    public static void condition(boolean predicate, String message) {
        if (!predicate) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Assert that the supplied {@code predicate} is {@code true}.
     *
     * @param predicate       the predicate to check
     * @param messageSupplier precondition violation message supplier
     * @throws IllegalStateException if the predicate is {@code false}
     */
    public static void condition(boolean predicate, Supplier<String> messageSupplier) {
        if (!predicate) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }
}
