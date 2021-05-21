package cn.sliew.milky.common.unit;

import cn.sliew.milky.common.primitives.Strings;

import java.util.Locale;

public class ByteValue implements Comparable<ByteValue> {

    public static final ByteValue ZERO = new ByteValue(0L, ByteUnit.BYTES);

    private final Long size;
    private final ByteUnit unit;

    public ByteValue(long bytes) {
        this(bytes, ByteUnit.BYTES);
    }

    public ByteValue(long size, ByteUnit unit) {
        this.size = size;
        this.unit = unit;
    }

    public long getBytes() {
        return unit.toBytes(size);
    }

    public long getKb() {
        return unit.toKB(size);
    }

    public long getMb() {
        return unit.toMB(size);
    }

    public long getGb() {
        return unit.toGB(size);
    }

    public long getTb() {
        return unit.toTB(size);
    }

    public long getPb() {
        return unit.toPB(size);
    }

    public double getKbFrac() {
        return ((double) getBytes()) / ByteUnit.C1;
    }

    public double getMbFrac() {
        return ((double) getBytes()) / ByteUnit.C2;
    }

    public double getGbFrac() {
        return ((double) getBytes()) / ByteUnit.C3;
    }

    public double getTbFrac() {
        return ((double) getBytes()) / ByteUnit.C4;
    }

    public double getPbFrac() {
        return ((double) getBytes()) / ByteUnit.C5;
    }

    public static ByteValue parseByteValue(String value) throws ValueParseException {
        return parseByteValue(value, null);
    }

    public static ByteValue parseByteValue(String value, ByteValue defaultValue) throws ValueParseException {
        if (value == null) {
            return defaultValue;
        }
        String lowerValue = value.toLowerCase(Locale.ROOT).trim();
        if (lowerValue.endsWith("k")) {
            return parse(value, lowerValue, "k", ByteUnit.KB);
        } else if (lowerValue.endsWith("kb")) {
            return parse(value, lowerValue, "kb", ByteUnit.KB);
        } else if (lowerValue.endsWith("m")) {
            return parse(value, lowerValue, "m", ByteUnit.MB);
        } else if (lowerValue.endsWith("mb")) {
            return parse(value, lowerValue, "mb", ByteUnit.MB);
        } else if (lowerValue.endsWith("g")) {
            return parse(value, lowerValue, "g", ByteUnit.GB);
        } else if (lowerValue.endsWith("gb")) {
            return parse(value, lowerValue, "gb", ByteUnit.GB);
        } else if (lowerValue.endsWith("t")) {
            return parse(value, lowerValue, "t", ByteUnit.TB);
        } else if (lowerValue.endsWith("tb")) {
            return parse(value, lowerValue, "tb", ByteUnit.TB);
        } else if (lowerValue.endsWith("p")) {
            return parse(value, lowerValue, "p", ByteUnit.PB);
        } else if (lowerValue.endsWith("pb")) {
            return parse(value, lowerValue, "pb", ByteUnit.PB);
        } else if (lowerValue.endsWith("b")) {
            return new ByteValue(Long.parseLong(lowerValue.substring(0, lowerValue.length() - 1).trim()), ByteUnit.BYTES);
        } else if (lowerValue.equals("-1")) {
            // Allow this special value to be unit-less:
            return new ByteValue(-1, ByteUnit.BYTES);
        } else if (lowerValue.equals("0")) {
            // Allow this special value to be unit-less:
            return new ByteValue(0, ByteUnit.BYTES);
        } else {
            // Missing units:
            throw new ValueParseException(String.format("failed to parse value [{}] as a size in bytes: unit is missing or unrecognized", value));
        }
    }

    private static ByteValue parse(final String initialInput, final String normalized, final String suffix, ByteUnit unit) {
        final String s = normalized.substring(0, normalized.length() - suffix.length()).trim();
        try {
            return new ByteValue(Long.parseLong(s), unit);
        } catch (NumberFormatException e) {
            throw new ValueParseException(String.format("failed to parse value [{}] as a size in bytes", initialInput), e);
        }
    }

    @Override
    public int compareTo(ByteValue other) {
        long thisValue = size * unit.toBytes(1);
        long otherValue = other.size * other.unit.toBytes(1);
        return Long.compare(thisValue, otherValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return compareTo((ByteValue) o) == 0;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(size * unit.toBytes(1));
    }

    @Override
    public String toString() {
        long bytes = getBytes();
        double value = bytes;
        String suffix = ByteUnit.BYTES.getSuffix();
        if (bytes >= ByteUnit.C5) {
            value = getPbFrac();
            suffix = ByteUnit.PB.getSuffix();
        } else if (bytes >= ByteUnit.C4) {
            value = getTbFrac();
            suffix = ByteUnit.TB.getSuffix();
        } else if (bytes >= ByteUnit.C3) {
            value = getGbFrac();
            suffix = ByteUnit.GB.getSuffix();
        } else if (bytes >= ByteUnit.C2) {
            value = getMbFrac();
            suffix = ByteUnit.MB.getSuffix();
        } else if (bytes >= ByteUnit.C1) {
            value = getKbFrac();
            suffix = ByteUnit.KB.getSuffix();
        }
        return Strings.format1Decimals(value, suffix);
    }
}
