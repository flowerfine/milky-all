package cn.sliew.milky.common.unit;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import static cn.sliew.milky.common.unit.ByteUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class ByteUnitTestCase extends MilkyTestCase {

    @Test
    void testBytes() {
        assertThat(BYTES.toBytes(1), equalTo(1L));
        assertThat(BYTES.toKB(1024), equalTo(1L));
        assertThat(BYTES.toMB(1024 * 1024), equalTo(1L));
        assertThat(BYTES.toGB(1024 * 1024 * 1024), equalTo(1L));
        // long size overflow
//        assertThat(BYTES.toTB(1024 * 1024 * 1024 * 1024), equalTo(1L));
//        assertThat(BYTES.toPB(1024 * 1024 * 1024 * 1024 * 1024), equalTo(1L));
    }

    @Test
    void testKB() {
        assertThat(KB.toBytes(1), equalTo(1024L));
        assertThat(KB.toKB(1), equalTo(1L));
        assertThat(KB.toMB(1024), equalTo(1L));
        assertThat(KB.toGB(1024 * 1024), equalTo(1L));
    }

    @Test
    void testMB() {
        assertThat(MB.toBytes(1), equalTo(1024L * 1024));
        assertThat(MB.toKB(1), equalTo(1024L));
        assertThat(MB.toMB(1), equalTo(1L));
        assertThat(MB.toGB(1024), equalTo(1L));
    }

    @Test
    void testGB() {
        assertThat(GB.toBytes(1), equalTo(1024L * 1024 * 1024));
        assertThat(GB.toKB(1), equalTo(1024L * 1024));
        assertThat(GB.toMB(1), equalTo(1024L));
        assertThat(GB.toGB(1), equalTo(1L));
    }

    @Test
    void testTB() {
        assertThat(TB.toBytes(1), equalTo(1024L * 1024 * 1024 * 1024));
        assertThat(TB.toKB(1), equalTo(1024L * 1024 * 1024));
        assertThat(TB.toMB(1), equalTo(1024L * 1024));
        assertThat(TB.toGB(1), equalTo(1024L));
        assertThat(TB.toTB(1), equalTo(1L));
    }

    @Test
    void testPB() {
        assertThat(PB.toBytes(1), equalTo(1024L * 1024 * 1024 * 1024 * 1024));
        assertThat(PB.toKB(1), equalTo(1024L * 1024 * 1024 * 1024));
        assertThat(PB.toMB(1), equalTo(1024L * 1024 * 1024));
        assertThat(PB.toGB(1), equalTo(1024L * 1024));
        assertThat(PB.toTB(1), equalTo(1024L));
        assertThat(PB.toPB(1), equalTo(1L));
    }

    @Test
    void testToString() {
        int v = randomIntBetween(1, 1023);
        assertThat(new ByteValue(PB.toBytes(v)).toString(), equalTo(v + "pb"));
        assertThat(new ByteValue(TB.toBytes(v)).toString(), equalTo(v + "tb"));
        assertThat(new ByteValue(GB.toBytes(v)).toString(), equalTo(v + "gb"));
        assertThat(new ByteValue(MB.toBytes(v)).toString(), equalTo(v + "mb"));
        assertThat(new ByteValue(KB.toBytes(v)).toString(), equalTo(v + "kb"));
        assertThat(new ByteValue(BYTES.toBytes(v)).toString(), equalTo(v + "b"));
    }

    @Test
    void testFromUnknownId() {
        try {
            final byte randomId = (byte) randomIntBetween(ByteUnit.values().length + 1, 100);
            ByteUnit.fromOrdinal(randomId);
            fail("Expected Unknown ByteSizeUnit");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("No byte size unit found for ordinal id"));
        }
    }

}
