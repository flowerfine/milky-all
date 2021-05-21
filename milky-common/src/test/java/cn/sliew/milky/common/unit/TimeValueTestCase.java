
package cn.sliew.milky.common.unit;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.object.HasToString.hasToString;
import static org.junit.jupiter.api.Assertions.*;

public class TimeValueTestCase extends MilkyTestCase {

    @Test
    void testSimple() {
        assertThat(TimeUnit.NANOSECONDS.toNanos(10), equalTo(new TimeValue(10, TimeUnit.NANOSECONDS).nanos()));
        assertThat(TimeUnit.MICROSECONDS.toMicros(10), equalTo(new TimeValue(10, TimeUnit.MICROSECONDS).micros()));
        assertThat(TimeUnit.MILLISECONDS.toMillis(10), equalTo(new TimeValue(10, TimeUnit.MILLISECONDS).millis()));
        assertThat(TimeUnit.MICROSECONDS.toMicros(10), equalTo(new TimeValue(10, TimeUnit.MICROSECONDS).micros()));
        assertThat(TimeUnit.SECONDS.toSeconds(10), equalTo(new TimeValue(10, TimeUnit.SECONDS).seconds()));
        assertThat(TimeUnit.MINUTES.toMinutes(10), equalTo(new TimeValue(10, TimeUnit.MINUTES).minutes()));
        assertThat(TimeUnit.HOURS.toHours(10), equalTo(new TimeValue(10, TimeUnit.HOURS).hours()));
        assertThat(TimeUnit.DAYS.toDays(10), equalTo(new TimeValue(10, TimeUnit.DAYS).days()));
    }

    @Test
    void testToString() {
        assertThat("10ms", equalTo(new TimeValue(10, TimeUnit.MILLISECONDS).toString()));
        assertThat("1.5s", equalTo(new TimeValue(1533, TimeUnit.MILLISECONDS).toString()));
        assertThat("1.5m", equalTo(new TimeValue(90, TimeUnit.SECONDS).toString()));
        assertThat("1.5h", equalTo(new TimeValue(90, TimeUnit.MINUTES).toString()));
        assertThat("1.5d", equalTo(new TimeValue(36, TimeUnit.HOURS).toString()));
        assertThat("1d", equalTo(new TimeValue(36, TimeUnit.HOURS).toHumanReadableString(0)));
        assertThat("1d", equalTo(new TimeValue(36, TimeUnit.HOURS).toHumanReadableString(-4)));
        assertThat("1.5d", equalTo(new TimeValue(36, TimeUnit.HOURS).toHumanReadableString(2)));
        assertThat("1.45d", equalTo(new TimeValue(35, TimeUnit.HOURS).toHumanReadableString(2)));
        assertThat("1.4583333333d", equalTo(new TimeValue(35, TimeUnit.HOURS).toHumanReadableString(10)));
        assertThat("1d", equalTo(new TimeValue(103723200, TimeUnit.MILLISECONDS).toHumanReadableString(0)));
        assertThat("1.2d", equalTo(new TimeValue(103723200, TimeUnit.MILLISECONDS).toHumanReadableString(1)));
        assertThat("1.2d", equalTo(new TimeValue(103723200, TimeUnit.MILLISECONDS).toHumanReadableString(2)));
        assertThat("1.2d", equalTo(new TimeValue(103723200, TimeUnit.MILLISECONDS).toHumanReadableString(3)));
        assertThat("1.2005d", equalTo(new TimeValue(103723200, TimeUnit.MILLISECONDS).toHumanReadableString(4)));
        assertThat("1d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(0)));
        assertThat("1d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(1)));
        assertThat("1d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(2)));
        assertThat("1d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(3)));
        assertThat("1d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(4)));
        assertThat("1d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(5)));
        assertThat("1d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(6)));
        assertThat("1.0000008d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(7)));
        assertThat("1.00000089d", equalTo(new TimeValue(86400077, TimeUnit.MILLISECONDS).toHumanReadableString(8)));
        assertThat("1.4583333333d", equalTo(new TimeValue(35, TimeUnit.HOURS).toHumanReadableString(Integer.MAX_VALUE)));
        assertThat("1000d", equalTo(new TimeValue(1000, TimeUnit.DAYS).toString()));
    }

    @Test
    void testMinusOne() {
        assertThat(new TimeValue(-1).nanos(), lessThan(0L));
    }

    @Test
    void testParseTimeValue() {
        // Space is allowed before unit:
        assertEquals(new TimeValue(10, TimeUnit.MILLISECONDS), TimeValue.parseTimeValue("10 ms", null));
        assertEquals(new TimeValue(10, TimeUnit.MILLISECONDS), TimeValue.parseTimeValue("10ms", null));
        assertEquals(new TimeValue(10, TimeUnit.MILLISECONDS), TimeValue.parseTimeValue("10 MS", null));
        assertEquals(new TimeValue(10, TimeUnit.MILLISECONDS), TimeValue.parseTimeValue("10MS", null));

        assertEquals(new TimeValue(10, TimeUnit.SECONDS), TimeValue.parseTimeValue("10 s", null));
        assertEquals(new TimeValue(10, TimeUnit.SECONDS), TimeValue.parseTimeValue("10s", null));
        assertEquals(new TimeValue(10, TimeUnit.SECONDS), TimeValue.parseTimeValue("10 S", null));
        assertEquals(new TimeValue(10, TimeUnit.SECONDS), TimeValue.parseTimeValue("10S", null));

        assertEquals(new TimeValue(10, TimeUnit.MINUTES), TimeValue.parseTimeValue("10 m", null));
        assertEquals(new TimeValue(10, TimeUnit.MINUTES), TimeValue.parseTimeValue("10m", null));
        // Time values of months should throw an exception as months are not
        // supported. Note that this is the only unit that is not case sensitive
        // as `m` is the only character that is overloaded in terms of which
        // time unit is expected between the upper and lower case versions
        expectThrows(IllegalArgumentException.class, () -> TimeValue.parseTimeValue("10 M", null));
        expectThrows(IllegalArgumentException.class, () -> TimeValue.parseTimeValue("10M", null));


        assertEquals(new TimeValue(10, TimeUnit.HOURS), TimeValue.parseTimeValue("10 h", null));
        assertEquals(new TimeValue(10, TimeUnit.HOURS), TimeValue.parseTimeValue("10h", null));
        assertEquals(new TimeValue(10, TimeUnit.HOURS), TimeValue.parseTimeValue("10 H", null));
        assertEquals(new TimeValue(10, TimeUnit.HOURS), TimeValue.parseTimeValue("10H", null));

        assertEquals(new TimeValue(10, TimeUnit.DAYS), TimeValue.parseTimeValue("10 d", null));
        assertEquals(new TimeValue(10, TimeUnit.DAYS), TimeValue.parseTimeValue("10d", null));
        assertEquals(new TimeValue(10, TimeUnit.DAYS), TimeValue.parseTimeValue("10 D", null));
        assertEquals(new TimeValue(10, TimeUnit.DAYS), TimeValue.parseTimeValue("10D", null));

        final int length = randomIntBetween(0, 8);
        final String zeros = new String(new char[length]).replace('\0', '0');
        assertTrue(TimeValue.parseTimeValue("-" + zeros + "1", null) == TimeValue.MINUS_ONE);
        assertTrue(TimeValue.parseTimeValue(zeros + "0", null) == TimeValue.ZERO);
    }

    @Test
    void testRoundTrip() {
        final String s = randomTimeValue();
        assertThat(TimeValue.parseTimeValue(s, null).getStringRep(), equalTo(s));
        final TimeValue t = new TimeValue(randomIntBetween(1, 128), randomFrom(TimeUnit.values()));
        assertThat(TimeValue.parseTimeValue(t.getStringRep(), null), equalTo(t));
    }

    private static final String FRACTIONAL_TIME_VALUES_ARE_NOT_SUPPORTED = "fractional time values are not supported";

    @Test
    void testNonFractionalTimeValues() {
        final String s = randomAsciiLettersOfLength(10) + randomTimeUnit();
        final IllegalArgumentException e =
                expectThrows(IllegalArgumentException.class, () -> TimeValue.parseTimeValue(s, null));
        assertThat(e, hasToString(containsString("failed to parse [" + s + "]")));
        assertThat(e, not(hasToString(containsString(FRACTIONAL_TIME_VALUES_ARE_NOT_SUPPORTED))));
        assertThat(e.getCause(), instanceOf(NumberFormatException.class));
    }

    @Test
    void testFractionalTimeValues() {
        double value;
        do {
            value = randomDouble();
        } while (value == 0);
        final String s = randomIntBetween(0, 128) + value + randomTimeUnit();
        final IllegalArgumentException e =
                expectThrows(IllegalArgumentException.class, () -> TimeValue.parseTimeValue(s, null));
        assertThat(e, hasToString(containsString("failed to parse [" + s + "]")));
        assertThat(e, hasToString(containsString(FRACTIONAL_TIME_VALUES_ARE_NOT_SUPPORTED)));
        assertThat(e.getCause(), instanceOf(NumberFormatException.class));
    }

    private String randomTimeUnit() {
        return randomFrom(Arrays.asList("nanos", "micros", "ms", "s", "m", "h", "d"));
    }

    @Test
    void testFailOnUnknownUnits() {
        try {
            TimeValue.parseTimeValue("23tw", null);
            fail("Expected ValueParseException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("failed to parse"));
        }
    }

    @Test
    void testFailOnMissingUnits() {
        try {
            TimeValue.parseTimeValue("42", null);
            fail("Expected ValueParseException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("failed to parse"));
        }
    }

    @Test
    void testNoDotsAllowed() {
        try {
            TimeValue.parseTimeValue("42ms.", null);
            fail("Expected ValueParseException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("failed to parse"));
        }
    }

    @Test
    void testToStringRep() {
        assertEquals("-1", new TimeValue(-1).getStringRep());
        assertEquals("10ms", new TimeValue(10, TimeUnit.MILLISECONDS).getStringRep());
        assertEquals("1533ms", new TimeValue(1533, TimeUnit.MILLISECONDS).getStringRep());
        assertEquals("90s", new TimeValue(90, TimeUnit.SECONDS).getStringRep());
        assertEquals("90m", new TimeValue(90, TimeUnit.MINUTES).getStringRep());
        assertEquals("36h", new TimeValue(36, TimeUnit.HOURS).getStringRep());
        assertEquals("1000d", new TimeValue(1000, TimeUnit.DAYS).getStringRep());
    }

    @Test
    void testCompareEquality() {
        long randomLong = randomNonNegativeLong();
        TimeUnit randomUnit = randomFrom(TimeUnit.values());
        TimeValue firstValue = new TimeValue(randomLong, randomUnit);
        TimeValue secondValue = new TimeValue(randomLong, randomUnit);
        assertEquals(0, firstValue.compareTo(secondValue));
    }

    @Test
    void testCompareValue() {
        long firstRandom = randomNonNegativeLong();
        long secondRandom = randomValueOtherThan(firstRandom, MilkyTestCase::randomNonNegativeLong);
        TimeUnit unit = randomFrom(TimeUnit.values());
        TimeValue firstValue = new TimeValue(firstRandom, unit);
        TimeValue secondValue = new TimeValue(secondRandom, unit);
        assertEquals(firstRandom > secondRandom, firstValue.compareTo(secondValue) > 0);
        assertEquals(secondRandom > firstRandom, secondValue.compareTo(firstValue) > 0);
    }

    @Test
    void testCompareUnits() {
        long number = randomNonNegativeLong();
        TimeUnit randomUnit = randomValueOtherThan(TimeUnit.DAYS, () -> randomFrom(TimeUnit.values()));
        TimeValue firstValue = new TimeValue(number, randomUnit);
        TimeValue secondValue = new TimeValue(number, TimeUnit.DAYS);
        assertTrue(firstValue.compareTo(secondValue) < 0);
        assertTrue(secondValue.compareTo(firstValue) > 0);
    }

    @Test
    void testConversionHashCode() {
        TimeValue firstValue = new TimeValue(randomIntBetween(0, Integer.MAX_VALUE), TimeUnit.MINUTES);
        TimeValue secondValue = new TimeValue(firstValue.getSeconds(), TimeUnit.SECONDS);
        assertEquals(firstValue.hashCode(), secondValue.hashCode());
    }

    private TimeUnit randomTimeUnitObject() {
        return randomFrom(Arrays.asList(TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS,
                TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS));
    }
}
