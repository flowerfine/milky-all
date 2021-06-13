package cn.sliew.milky.test.extension.random;

import cn.sliew.milky.test.extension.random.generators.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ExtendWith(RandomizedExtension.class)
public class RandomizedTestCase {

    /**
     * The global multiplier property (Double).
     *
     * @see #multiplier()
     */
    public static final String SYSPROP_MULTIPLIER = "randomized.multiplier";

    /**
     * Default multiplier.
     *
     * @see #SYSPROP_MULTIPLIER
     */
    private static final double DEFAULT_MULTIPLIER = 1.0d;

    /**
     * Shortcut for {@link RandomizedContext#current()}.
     */
    public static RandomizedContext getContext() {
        return RandomizedContext.current();
    }

    /**
     * Returns true if {@link Nightly} test group is enabled.
     *
     * @see Nightly
     */
    public static boolean isNightly() {
        return false;
    }

    /**
     * Shortcut for {@link RandomizedContext#getRandomness()#getRandom()} ()}. Even though this method
     * is static, it returns per-thread {@link Random} instance, so no race conditions
     * can occur.
     *
     * <p>It is recommended that specific methods are used to pick random values.
     */
    public static Random getRandom() {
        return getContext().getRandomness().getRandom();
    }

    //
    // Random value pickers. Shortcuts to methods in {@link #getRandom()} mostly.
    //
    public static boolean randomBoolean() {
        return getRandom().nextBoolean();
    }

    public static byte randomByte() {
        return (byte) getRandom().nextInt();
    }

    public static short randomShort() {
        return (short) getRandom().nextInt();
    }

    public static int randomInt() {
        return getRandom().nextInt();
    }

    public static float randomFloat() {
        return getRandom().nextFloat();
    }

    public static double randomDouble() {
        return getRandom().nextDouble();
    }

    public static long randomLong() {
        return getRandom().nextLong();
    }

    /**
     * @see Random#nextGaussian()
     */
    public static double randomGaussian() {
        return getRandom().nextGaussian();
    }


    //
    // Biased value pickers.
    //
    /**
     * A biased "evil" random float between min and max (inclusive).
     *
     * @see BiasedNumbers#randomFloatBetween(Random, float, float)
     */
    public static float biasedFloatBetween(float min, float max) {
        return BiasedNumbers.randomFloatBetween(getRandom(), min, max);
    }

    /**
     * A biased "evil" random double between min and max (inclusive).
     *
     * @see BiasedNumbers#randomDoubleBetween(Random, double, double)
     */
    public static double biasedDoubleBetween(double min, double max) {
        return BiasedNumbers.randomDoubleBetween(getRandom(), min, max);
    }

    //
    // Delegates to RandomBytes.
    //
    /**
     * Returns a byte array with random content.
     *
     * @param length The length of the byte array. Can be zero.
     * @return Returns a byte array with random content.
     */
    public static byte[] randomBytesOfLength(int length) {
        return RandomBytes.randomBytesOfLength(new Random(getRandom().nextLong()), length);
    }

    /**
     * Returns a byte array with random content.
     *
     * @param minLength The minimum length of the byte array. Can be zero.
     * @param maxLength The maximum length of the byte array. Can be zero.
     * @return Returns a byte array with random content.
     */
    public static byte[] randomBytesOfLength(int minLength, int maxLength) {
        return RandomBytes.randomBytesOfLengthBetween(new Random(getRandom().nextLong()), minLength, maxLength);
    }

    //
    // Delegates to RandomNumbers.
    //
    public static long randomNonNegativeLong() {
        long randomLong = randomLong();
        return randomLong == Long.MIN_VALUE ? 0 : Math.abs(randomLong);
    }

    /**
     * A random integer from <code>min</code> to <code>max</code> (inclusive).
     *
     * @see #scaledRandomIntBetween(int, int)
     */
    public static int randomIntBetween(int min, int max) {
        return RandomNumbers.randomIntBetween(getRandom(), min, max);
    }

    /**
     * An alias for {@link #randomIntBetween(int, int)}.
     *
     * @see #scaledRandomIntBetween(int, int)
     */
    public static int between(int min, int max) {
        return randomIntBetween(min, max);
    }

    /**
     * A random long from <code>min</code> to <code>max</code> (inclusive).
     */
    public static long randomLongBetween(long min, long max) {
        return RandomNumbers.randomLongBetween(getRandom(), min, max);
    }

    /**
     * An alias for {@link #randomLongBetween}.
     */
    public static long between(long min, long max) {
        return randomLongBetween(min, max);
    }

    /**
     * Returns a random value greater or equal to <code>min</code>. The value
     * picked is affected by {@link #isNightly()} and {@link #multiplier()}.
     *
     * @see #scaledRandomIntBetween(int, int)
     */
    public static int atLeast(int min) {
        if (min < 0) throw new IllegalArgumentException("atLeast requires non-negative argument: " + min);
        return scaledRandomIntBetween(min, Integer.MAX_VALUE);
    }

    /**
     * Returns a non-negative random value smaller or equal <code>max</code>. The value
     * picked is affected by {@link #isNightly()} and {@link #multiplier()}.
     *
     * <p>This method is effectively an alias to:
     * <pre>
     * scaledRandomIntBetween(0, max)
     * </pre>
     *
     * @see #scaledRandomIntBetween(int, int)
     */
    public static int atMost(int max) {
        if (max < 0) throw new IllegalArgumentException("atMost requires non-negative argument: " + max);
        return scaledRandomIntBetween(0, max);
    }

    /**
     * Rarely returns <code>true</code> in about 10% of all calls (regardless of the
     * {@link #isNightly()} mode).
     */
    public static boolean rarely() {
        return randomIntBetween(0, 100) >= 90;
    }

    /**
     * The exact opposite of {@link #rarely()}.
     */
    public static boolean frequently() {
        return !rarely();
    }

    //
    // Delegates to RandomPicks
    //
    /**
     * Pick a random object from the given array. The array must not be empty.
     */
    public static <T> T randomFrom(T[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    /**
     * Pick a random object from the given list.
     */
    public static <T> T randomFrom(List<T> list) {
        return RandomPicks.randomFrom(getRandom(), list);
    }

    public static byte randomFrom(byte[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    public static short randomFrom(short[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    public static int randomFrom(int[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    public static char randomFrom(char[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    public static float randomFrom(float[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    public static long randomFrom(long[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    public static double randomFrom(double[] array) {
        return RandomPicks.randomFrom(getRandom(), array);
    }

    /**
     * Returns size random values
     */
    public static <T> List<T> randomSubsetOf(int size, T... values) {
        List<T> list = Arrays.asList(values);
        return randomSubsetOf(size, list);
    }

    /**
     * Returns a random subset of values (including a potential empty list, or the full original list)
     */
    public static <T> List<T> randomSubsetOf(Collection<T> collection) {
        return randomSubsetOf(randomIntBetween(0, collection.size()), collection);
    }

    /**
     * Returns size random values
     */
    public static <T> List<T> randomSubsetOf(int size, Collection<T> collection) {
        if (size > collection.size()) {
            throw new IllegalArgumentException("Can\'t pick " + size + " random objects from a collection of " +
                    collection.size() + " objects");
        }
        List<T> tempList = new ArrayList<>(collection);
        Collections.shuffle(tempList, getRandom());
        return tempList.subList(0, size);
    }

    /**
     * helper to get a random value in a certain range that's different from the input
     */
    public static <T> T randomValueOtherThan(T input, Supplier<T> randomSupplier) {
        return randomValueOtherThanMany(v -> Objects.equals(input, v), randomSupplier);
    }

    /**
     * helper to get a random value in a certain range that's different from the input
     */
    public static <T> T randomValueOtherThanMany(Predicate<T> input, Supplier<T> randomSupplier) {
        T randomValue = null;
        do {
            randomValue = randomSupplier.get();
        } while (input.test(randomValue));
        return randomValue;
    }

    //
    // "multiplied" or scaled value pickers. These will be affected by global multiplier.
    //
    /**
     * A multiplier can be used to linearly scale certain values. It can be used to make data
     * or iterations of certain tests "heavier" for nightly runs, for example.
     *
     * <p>The default multiplier value is 1.</p>
     *
     * @see #SYSPROP_MULTIPLIER
     */
    public static double multiplier() {
        return systemPropertyAsDouble(SYSPROP_MULTIPLIER, DEFAULT_MULTIPLIER);
    }

    /**
     * Returns a "scaled" number of iterations for loops which can have a variable
     * iteration count. This method is effectively
     * an alias to {@link #scaledRandomIntBetween(int, int)}.
     */
    public static int iterations(int min, int max) {
        return scaledRandomIntBetween(min, max);
    }

    /**
     * Returns a "scaled" random number between min and max (inclusive). The number of
     * iterations will fall between [min, max], but the selection will also try to
     * achieve the points below:
     * <ul>
     *   <li>the multiplier can be used to move the number of iterations closer to min
     *   (if it is smaller than 1) or closer to max (if it is larger than 1). Setting
     *   the multiplier to 0 will always result in picking min.</li>
     *   <li>on normal runs, the number will be closer to min than to max.</li>
     *   <li>on nightly runs, the number will be closer to max than to min.</li>
     * </ul>
     *
     * @param min Minimum (inclusive).
     * @param max Maximum (inclusive).
     * @return Returns a random number between min and max.
     * @see #multiplier()
     */
    public static int scaledRandomIntBetween(int min, int max) {
        if (min < 0) throw new IllegalArgumentException("min must be >= 0: " + min);
        if (min > max) throw new IllegalArgumentException("max must be >= min: " + min + ", " + max);

        double point = Math.min(1, Math.abs(randomGaussian()) * 0.3) * multiplier();
        double range = max - min;
        int scaled = (int) Math.round(Math.min(point * range, range));
        if (isNightly()) {
            return max - scaled;
        } else {
            return min + scaled;
        }
    }

    // Methods to help with environment.
    /**
     * Return a random Locale from the available locales on the system.
     *
     * <p>Warning: This test assumes the returned array of locales is repeatable from jvm execution
     * to jvm execution. It _may_ be different from jvm to jvm and as such, it can render
     * tests execute in a different way.</p>
     */
    public static Locale randomLocale() {
        Locale[] availableLocales = Locale.getAvailableLocales();
        Arrays.sort(availableLocales, new Comparator<Locale>() {
            public int compare(Locale o1, Locale o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return randomFrom(availableLocales);
    }

    /**
     * Return a random TimeZone from the available timezones on the system.
     *
     * <p>Warning: This test assumes the returned array of time zones is repeatable from jvm execution
     * to jvm execution. It _may_ be different from jvm to jvm and as such, it can render
     * tests execute in a different way.</p>
     */
    public static TimeZone randomTimeZone() {
        final String[] availableIDs = TimeZone.getAvailableIDs();
        Arrays.sort(availableIDs);
        return TimeZone.getTimeZone(randomFrom(availableIDs));
    }

    //
    // Characters and strings. Delegates to RandomStrings and that in turn to StringGenerators.
    //
    /**
     * @see RandomStrings#randomAsciiLettersOfLengthBetween
     */
    public static String randomAsciiLettersOfLengthBetween(int minLetters, int maxLetters) {
        return RandomStrings.randomAsciiLettersOfLengthBetween(getRandom(), minLetters, maxLetters);
    }

    /**
     * @see RandomStrings#randomAsciiLettersOfLength
     */
    public static String randomAsciiLettersOfLength(int codeUnits) {
        return RandomStrings.randomAsciiLettersOfLength(getRandom(), codeUnits);
    }

    /**
     * @see RandomStrings#randomAsciiAlphanumOfLengthBetween
     */
    public static String randomAsciiAlphanumOfLengthBetween(int minCodeUnits, int maxCodeUnits) {
        return RandomStrings.randomAsciiAlphanumOfLengthBetween(getRandom(), minCodeUnits, maxCodeUnits);
    }

    /**
     * @see RandomStrings#randomAsciiAlphanumOfLength
     */
    public static String randomAsciiAlphanumOfLength(int codeUnits) {
        return RandomStrings.randomAsciiAlphanumOfLength(getRandom(), codeUnits);
    }

    /**
     * @see RandomStrings#randomUnicodeOfLengthBetween
     */
    public static String randomUnicodeOfLengthBetween(int minCodeUnits, int maxCodeUnits) {
        return RandomStrings.randomUnicodeOfLengthBetween(getRandom(),
                minCodeUnits, maxCodeUnits);
    }

    /**
     * @see RandomStrings#randomUnicodeOfLength
     */
    public static String randomUnicodeOfLength(int codeUnits) {
        return RandomStrings.randomUnicodeOfLength(getRandom(), codeUnits);
    }

    /**
     * @see RandomStrings#randomUnicodeOfCodepointLengthBetween
     */
    public static String randomUnicodeOfCodepointLengthBetween(int minCodePoints, int maxCodePoints) {
        return RandomStrings.randomUnicodeOfCodepointLengthBetween(getRandom(),
                minCodePoints, maxCodePoints);
    }

    /**
     * @see RandomStrings#randomUnicodeOfCodepointLength
     */
    public static String randomUnicodeOfCodepointLength(int codePoints) {
        return RandomStrings.randomUnicodeOfCodepointLength(getRandom(), codePoints);
    }

    /**
     * @see RandomStrings#randomRealisticUnicodeOfLengthBetween
     */
    public static String randomRealisticUnicodeOfLengthBetween(int minCodeUnits, int maxCodeUnits) {
        return RandomStrings.randomRealisticUnicodeOfLengthBetween(getRandom(),
                minCodeUnits, maxCodeUnits);
    }

    /**
     * @see RandomStrings#randomRealisticUnicodeOfLength
     */
    public static String randomRealisticUnicodeOfLength(int codeUnits) {
        return RandomStrings.randomRealisticUnicodeOfLength(getRandom(), codeUnits);
    }

    /**
     * @see RandomStrings#randomRealisticUnicodeOfCodepointLengthBetween
     */
    public static String randomRealisticUnicodeOfCodepointLengthBetween(
            int minCodePoints, int maxCodePoints) {
        return RandomStrings.randomRealisticUnicodeOfCodepointLengthBetween(
                getRandom(), minCodePoints, maxCodePoints);
    }

    /**
     * @see RandomStrings#randomRealisticUnicodeOfCodepointLength
     */
    public static String randomRealisticUnicodeOfCodepointLength(int codePoints) {
        return RandomStrings.randomRealisticUnicodeOfCodepointLength(getRandom(),
                codePoints);
    }

    private static final String[] TIME_SUFFIXES = new String[]{"d", "h", "ms", "s", "m", "micros", "nanos"};

    public static String randomTimeValue(int lower, int upper, String... suffixes) {
        return randomIntBetween(lower, upper) + randomFrom(suffixes);
    }

    public static String randomTimeValue(int lower, int upper) {
        return randomTimeValue(lower, upper, TIME_SUFFIXES);
    }

    public static String randomTimeValue() {
        return randomTimeValue(0, 1000);
    }

    public static String randomPositiveTimeValue() {
        return randomTimeValue(1, 1000);
    }

    //
    // wrappers for utility methods elsewhere that don't require try..catch blocks
    // and rethrow the original checked exception if needed. dirty a bit, but saves
    // keystrokes...
    //
    /**
     * Same as {@link Thread#sleep(long)}.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Rethrow.rethrow(e);
        }
    }

    //
    // System properties and their conversion to common types, with defaults.
    //
    /**
     * Get a system property and convert it to a double, if defined. Otherwise, return the default value.
     */
    public static double systemPropertyAsDouble(String propertyName, double defaultValue) {
        String v = System.getProperty(propertyName);
        if (v != null && !v.trim().isEmpty()) {
            try {
                return Double.parseDouble(v.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Double value expected for property " +
                        propertyName + ": " + v, e);
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a system property and convert it to a float, if defined. Otherwise, return the default value.
     */
    public static float systemPropertyAsFloat(String propertyName, float defaultValue) {
        String v = System.getProperty(propertyName);
        if (v != null && !v.trim().isEmpty()) {
            try {
                return Float.parseFloat(v.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Float value expected for property " +
                        propertyName + ": " + v, e);
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a system property and convert it to an int, if defined. Otherwise, return the default value.
     */
    public static int systemPropertyAsInt(String propertyName, int defaultValue) {
        String v = System.getProperty(propertyName);
        if (v != null && !v.trim().isEmpty()) {
            try {
                return Integer.parseInt(v.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Integer value expected for property " +
                        propertyName + ": " + v, e);
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a system property and convert it to a long, if defined. Otherwise, return the default value.
     */
    public static float systemPropertyAsLong(String propertyName, int defaultValue) {
        String v = System.getProperty(propertyName);
        if (v != null && !v.trim().isEmpty()) {
            try {
                return Long.parseLong(v.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Long value expected for property " +
                        propertyName + ": " + v, e);
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Boolean constants mapping.
     */
    @SuppressWarnings("serial")
    private final static HashMap<String, Boolean> BOOLEANS = new HashMap<String, Boolean>() {{
        put("true", true);
        put("false", false);
        put("on", true);
        put("off", false);
        put("yes", true);
        put("no", false);
        put("enabled", true);
        put("disabled", false);
    }};

    /**
     * Get a system property and convert it to a boolean, if defined. This method returns
     * <code>true</code> if the property exists an is set to any of the following strings
     * (case-insensitive): <code>true</code>, <code>on</code>, <code>yes</code>, <code>enabled</code>.
     *
     * <p><code>false</code> is returned if the property exists and is set to any of the
     * following strings (case-insensitive):
     * <code>false</code>, <code>off</code>, <code>no</code>, <code>disabled</code>.
     */
    public static boolean systemPropertyAsBoolean(String propertyName, boolean defaultValue) {
        String v = System.getProperty(propertyName);

        if (v != null && !v.trim().isEmpty()) {
            v = v.trim();
            Boolean result = BOOLEANS.get(v);
            if (result != null)
                return result.booleanValue();
            else
                throw new IllegalArgumentException("Boolean value expected for property " +
                        propertyName + " " +
                        "(true/false, on/off, enabled/disabled, yes/no): " + v);
        } else {
            return defaultValue;
        }
    }
}
