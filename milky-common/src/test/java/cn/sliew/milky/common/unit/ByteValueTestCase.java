package cn.sliew.milky.common.unit;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ByteValueTestCase extends MilkyTestCase {

    @Test
    void testActualPeta() {
        assertThat(new ByteValue(4, ByteUnit.PB).getBytes(), equalTo(4503599627370496L));
    }

    @Test
    void testActualTera() {
        assertThat(new ByteValue(4, ByteUnit.TB).getBytes(), equalTo(4398046511104L));
    }

    @Test
    void testActual() {
        assertThat(new ByteValue(4, ByteUnit.GB).getBytes(), equalTo(4294967296L));
    }

    @Test
    void testSimple() {
        assertThat(ByteUnit.BYTES.toBytes(10), is(new ByteValue(10, ByteUnit.BYTES).getBytes()));
        assertThat(ByteUnit.KB.toKB(10), is(new ByteValue(10, ByteUnit.KB).getKb()));
        assertThat(ByteUnit.MB.toMB(10), is(new ByteValue(10, ByteUnit.MB).getMb()));
        assertThat(ByteUnit.GB.toGB(10), is(new ByteValue(10, ByteUnit.GB).getGb()));
        assertThat(ByteUnit.TB.toTB(10), is(new ByteValue(10, ByteUnit.TB).getTb()));
        assertThat(ByteUnit.PB.toPB(10), is(new ByteValue(10, ByteUnit.PB).getPb()));
    }

    @Test
    void testEquality() {
        String[] equalValues = new String[]{"1GB", "1024MB", "1048576KB", "1073741824B"};
        ByteValue value1 = ByteValue.parseByteValue(randomFrom(equalValues), null);
        ByteValue value2 = ByteValue.parseByteValue(randomFrom(equalValues), null);
        assertThat(value1, equalTo(value2));
    }

    @Test
    public void testToString() {
        assertThat("10b", is(new ByteValue(10, ByteUnit.BYTES).toString()));
        assertThat("1.5kb", is(new ByteValue((long) (1024 * 1.5), ByteUnit.BYTES).toString()));
        assertThat("1.5mb", is(new ByteValue((long) (1024 * 1.5), ByteUnit.KB).toString()));
        assertThat("1.5gb", is(new ByteValue((long) (1024 * 1.5), ByteUnit.MB).toString()));
        assertThat("1.5tb", is(new ByteValue((long) (1024 * 1.5), ByteUnit.GB).toString()));
        assertThat("1.5pb", is(new ByteValue((long) (1024 * 1.5), ByteUnit.TB).toString()));
        assertThat("1536pb", is(new ByteValue((long) (1024 * 1.5), ByteUnit.PB).toString()));
    }

    @Test
    public void testParsing() {
        assertThat(ByteValue.parseByteValue("42PB").toString(), is("42pb"));
        assertThat(ByteValue.parseByteValue("42 PB").toString(), is("42pb"));
        assertThat(ByteValue.parseByteValue("42pb").toString(), is("42pb"));
        assertThat(ByteValue.parseByteValue("42 pb").toString(), is("42pb"));

        assertThat(ByteValue.parseByteValue("42P").toString(), is("42pb"));
        assertThat(ByteValue.parseByteValue("42 P").toString(), is("42pb"));
        assertThat(ByteValue.parseByteValue("42p").toString(), is("42pb"));
        assertThat(ByteValue.parseByteValue("42 p").toString(), is("42pb"));

        assertThat(ByteValue.parseByteValue("54TB").toString(), is("54tb"));
        assertThat(ByteValue.parseByteValue("54 TB").toString(), is("54tb"));
        assertThat(ByteValue.parseByteValue("54tb").toString(), is("54tb"));
        assertThat(ByteValue.parseByteValue("54 tb").toString(), is("54tb"));

        assertThat(ByteValue.parseByteValue("54T").toString(), is("54tb"));
        assertThat(ByteValue.parseByteValue("54 T").toString(), is("54tb"));
        assertThat(ByteValue.parseByteValue("54t").toString(), is("54tb"));
        assertThat(ByteValue.parseByteValue("54 t").toString(), is("54tb"));

        assertThat(ByteValue.parseByteValue("12GB").toString(), is("12gb"));
        assertThat(ByteValue.parseByteValue("12 GB").toString(), is("12gb"));
        assertThat(ByteValue.parseByteValue("12gb").toString(), is("12gb"));
        assertThat(ByteValue.parseByteValue("12 gb").toString(), is("12gb"));

        assertThat(ByteValue.parseByteValue("12G").toString(), is("12gb"));
        assertThat(ByteValue.parseByteValue("12 G").toString(), is("12gb"));
        assertThat(ByteValue.parseByteValue("12g").toString(), is("12gb"));
        assertThat(ByteValue.parseByteValue("12 g").toString(), is("12gb"));

        assertThat(ByteValue.parseByteValue("12M").toString(), is("12mb"));
        assertThat(ByteValue.parseByteValue("12 M").toString(), is("12mb"));
        assertThat(ByteValue.parseByteValue("12m").toString(), is("12mb"));
        assertThat(ByteValue.parseByteValue("12 m").toString(), is("12mb"));

        assertThat(ByteValue.parseByteValue("23KB").toString(), is("23kb"));
        assertThat(ByteValue.parseByteValue("23 KB").toString(), is("23kb"));
        assertThat(ByteValue.parseByteValue("23kb").toString(), is("23kb"));
        assertThat(ByteValue.parseByteValue("23 kb").toString(), is("23kb"));

        assertThat(ByteValue.parseByteValue("23K").toString(), is("23kb"));
        assertThat(ByteValue.parseByteValue("23 K").toString(), is("23kb"));
        assertThat(ByteValue.parseByteValue("23k").toString(), is("23kb"));
        assertThat(ByteValue.parseByteValue("23 k").toString(), is("23kb"));

        assertThat(ByteValue.parseByteValue("1B").toString(), is("1b"));
        assertThat(ByteValue.parseByteValue("1 B").toString(), is("1b"));
        assertThat(ByteValue.parseByteValue("1b").toString(), is("1b"));
        assertThat(ByteValue.parseByteValue("1 b").toString(), is("1b"));
    }

    @Test
    public void testFailOnMissingUnits() {
        Exception e = expectThrows(ValueParseException.class, () -> ByteValue.parseByteValue("23"));
        assertThat(e.getMessage(), containsString("failed to parse value [23]"));
    }

    @Test
    public void testFailOnUnknownUnits() {
        Exception e = expectThrows(ValueParseException.class, () -> ByteValue.parseByteValue("23jw"));
        assertThat(e.getMessage(), containsString("failed to parse value [23jw]"));
    }

    @Test
    public void testFailOnEmptyParsing() {
        Exception e = expectThrows(ValueParseException.class,
                () -> assertThat(ByteValue.parseByteValue("").toString(), is("23kb")));
        assertThat(e.getMessage(), containsString("failed to parse value []"));
    }

    @Test
    public void testFailOnEmptyNumberParsing() {
        Exception e = expectThrows(ValueParseException.class,
                () -> assertThat(ByteValue.parseByteValue("g").toString(), is("23b")));
        assertThat(e.getMessage(), containsString("failed to parse value [g]"));
    }

    @Test
    public void testNoDotsAllowed() {
        Exception e = expectThrows(ValueParseException.class, () -> ByteValue.parseByteValue("42b.", null));
        assertThat(e.getMessage(), containsString("failed to parse value [42b.]"));
    }

    @Test
    public void testCompareEquality() {
        ByteUnit randomUnit = randomFrom(ByteUnit.values());
        long firstRandom = randomNonNegativeLong() / randomUnit.toBytes(1);
        ByteValue firstByteValue = new ByteValue(firstRandom, randomUnit);
        ByteValue secondByteValue = new ByteValue(firstRandom, randomUnit);
        assertEquals(0, firstByteValue.compareTo(secondByteValue));
    }

    @Test
    public void testCompareValue() {
        ByteUnit unit = randomFrom(ByteUnit.values());
        long firstRandom = randomNonNegativeLong() / unit.toBytes(1);
        long secondRandom = randomValueOtherThan(firstRandom, () -> randomNonNegativeLong() / unit.toBytes(1));
        ByteValue firstByteValue = new ByteValue(firstRandom, unit);
        ByteValue secondByteValue = new ByteValue(secondRandom, unit);
        assertEquals(firstRandom > secondRandom, firstByteValue.compareTo(secondByteValue) > 0);
        assertEquals(secondRandom > firstRandom, secondByteValue.compareTo(firstByteValue) > 0);
    }

    @Test
    public void testCompareUnits() {
        long number = randomLongBetween(1, Long.MAX_VALUE / ByteUnit.PB.toBytes(1));
        ByteUnit randomUnit = randomValueOtherThan(ByteUnit.PB, () -> randomFrom(ByteUnit.values()));
        ByteValue firstByteValue = new ByteValue(number, randomUnit);
        ByteValue secondByteValue = new ByteValue(number, ByteUnit.PB);
        assertTrue(firstByteValue.compareTo(secondByteValue) < 0);
        assertTrue(secondByteValue.compareTo(firstByteValue) > 0);
    }

    @Test
    public void testOutOfRange() {
        // Make sure a value of > Long.MAX_VALUE bytes throws an exception
        ByteUnit unit = randomValueOtherThan(ByteUnit.BYTES, () -> randomFrom(ByteUnit.values()));
        long size = (long) randomDouble() * unit.toBytes(1) + (Long.MAX_VALUE - unit.toBytes(1));
        IllegalArgumentException exception = expectThrows(IllegalArgumentException.class, () -> new ByteValue(size, unit));
        assertEquals("Values greater than " + Long.MAX_VALUE + " bytes are not supported: " + size + unit.getSuffix(),
                exception.getMessage());

        // Make sure for units other than BYTES a size of -1 throws an exception
        ByteUnit unit2 = randomValueOtherThan(ByteUnit.BYTES, () -> randomFrom(ByteUnit.values()));
        long size2 = -1L;
        exception = expectThrows(IllegalArgumentException.class, () -> new ByteValue(size2, unit2));
        assertEquals("Values less than -1 bytes are not supported: " + size2 + unit2.getSuffix(), exception.getMessage());

        // Make sure for any unit a size < -1 throws an exception
        ByteUnit unit3 = randomFrom(ByteUnit.values());
        long size3 = -1L * randomNonNegativeLong() - 1L;
        exception = expectThrows(IllegalArgumentException.class, () -> new ByteValue(size3, unit3));
        assertEquals("Values less than -1 bytes are not supported: " + size3 + unit3.getSuffix(), exception.getMessage());
    }

    @Test
    public void testConversionHashCode() {
        ByteValue firstValue = new ByteValue(randomIntBetween(0, Integer.MAX_VALUE), ByteUnit.GB);
        ByteValue secondValue = new ByteValue(firstValue.getBytes(), ByteUnit.BYTES);
        assertEquals(firstValue.hashCode(), secondValue.hashCode());
    }

    @Test
    public void testParseInvalidValue() {
        ValueParseException exception = expectThrows(ValueParseException.class,
                () -> ByteValue.parseByteValue("-6mb"));
        assertEquals("failed to parse value [-6mb] as a size in bytes", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
    }
}
