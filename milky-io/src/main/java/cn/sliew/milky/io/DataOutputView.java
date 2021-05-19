package cn.sliew.milky.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public interface DataOutputView extends DataOutput, Serializable {

    void writeBytes(byte[] b) throws IOException;

    void writeBytes(byte[] b, int off, int len) throws IOException;

    @Override
    default void write(byte[] b) throws IOException {
        writeBytes(b);
    }

    @Override
    default void write(byte[] b, int off, int len) throws IOException {
        writeBytes(b, off, len);
    }

    byte ZERO = 0;
    byte ONE = 1;
    byte TWO = 2;

    /**
     * Writes a 1 byte boolean.
     */
    @Override
    default void writeBoolean(boolean v) throws IOException {
        writeByte(v ? ONE : ZERO);
    }

    /**
     * Writes an optional {@link Boolean}.
     */
    default void writeOptionalBoolean(Boolean b) throws IOException {
        if (b == null) {
            writeByte(TWO);
        } else {
            writeBoolean(b);
        }
    }

    /**
     * Writes a 2 byte short.
     */
    @Override
    default void writeShort(int v) throws IOException {
        final byte[] buffer = getBuffer();
        buffer[0] = (byte) (v >> 8);
        buffer[1] = (byte) v;
        writeBytes(buffer, 0, 2);
    }

    /**
     * Writes a 2 byte char.
     */
    @Override
    default void writeChar(int v) throws IOException {
        final byte[] buffer = getBuffer();
        buffer[0] = (byte) (v >>> 8);
        buffer[1] = (byte) v;
        writeBytes(buffer, 0, 2);
    }

    /**
     * Writes a 4 byte int.
     */
    @Override
    default void writeInt(int v) throws IOException {
        final byte[] buffer = getBuffer();
        buffer[0] = (byte) (v >> 24);
        buffer[1] = (byte) (v >> 16);
        buffer[2] = (byte) (v >> 8);
        buffer[3] = (byte) v;
        writeBytes(buffer, 0, 4);
    }

    /**
     * Writes an int in a variable-length format.
     * Writes between one and five bytes.  Smaller values take fewer bytes.
     * Negative numbers will always use all 5 bytes and are therefore better
     * serialized using {@link #writeInt} or {@link #writeZInt}
     */
    default void writeVInt(int v) throws IOException {
        if (Integer.numberOfLeadingZeros(v) >= 25) {
            writeByte((byte) v);
            return;
        }
        byte[] buffer = getBuffer();
        int index = 0;
        do {
            buffer[index++] = ((byte) ((v & 0x7f) | 0x80));
            v >>>= 7;
        } while ((v & ~0x7F) != 0);
        buffer[index++] = ((byte) v);
        writeBytes(buffer, 0, index);
    }

    /**
     * Writes a {@link BitUtil#zigZagEncode(int) zig-zag}-encoded
     * {@link #writeVInt(int) variable-length} integer. This is typically useful to
     * write small signed ints and is equivalent to calling {@code writeVInt(BitUtil.zigZagEncode(i))}.
     *
     * @see DataInputView#readZInt()
     */
    default void writeZInt(int v) throws IOException {
        writeVInt(BitUtil.zigZagEncode(v));
    }

    /**
     * Writes an optional {@link Integer}.
     */
    default void writeOptionalInt(Integer integer) throws IOException {
        if (integer == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeInt(integer);
        }
    }

    default void writeOptionalVInt(Integer integer) throws IOException {
        if (integer == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeVInt(integer);
        }
    }

    /**
     * Writes an 8 byte long.
     */
    default void writeLong(long i) throws IOException {
        final byte[] buffer = getBuffer();
        buffer[0] = (byte) (i >> 56);
        buffer[1] = (byte) (i >> 48);
        buffer[2] = (byte) (i >> 40);
        buffer[3] = (byte) (i >> 32);
        buffer[4] = (byte) (i >> 24);
        buffer[5] = (byte) (i >> 16);
        buffer[6] = (byte) (i >> 8);
        buffer[7] = (byte) i;
        writeBytes(buffer, 0, 8);
    }

    /**
     * Writes a long in a variable-length format.
     * Writes between one and ten bytes. Smaller values take fewer bytes.
     * Negative numbers use ten bytes so prefer {@link #writeLong(long)} or {@link #writeZLong(long)}
     * for negative numbers.
     */
    default void writeVLong(long i) throws IOException {
        final byte[] buffer = getBuffer();
        int index = 0;
        while ((i & ~0x7F) != 0) {
            buffer[index++] = ((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        }
        buffer[index++] = ((byte) i);
        writeBytes(buffer, 0, index);
    }

    /**
     * Writes a long in a variable-length format.
     * Writes between one and ten bytes. Values are remapped by sliding the sign bit into
     * the lsb and then encoded as an unsigned number
     * e.g., 0 -;&gt; 0, -1 -;&gt; 1, 1 -;&gt; 2, ..., Long.MIN_VALUE -;&gt; -1, Long.MAX_VALUE -;&gt; -2
     * Numbers with small absolute value will have a small encoding
     * If the numbers are known to be non-negative, use {@link #writeVLong(long)}
     */
    default void writeZLong(long i) throws IOException {
        final byte[] buffer = getBuffer();
        int index = 0;
        // zig-zag encoding cf. https://developers.google.com/protocol-buffers/docs/encoding?hl=en
        long value = BitUtil.zigZagEncode(i);
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
            buffer[index++] = (byte) ((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buffer[index++] = (byte) (value & 0x7F);
        writeBytes(buffer, 0, index);
    }

    /**
     * Writes an optional {@link Long}.
     */
    default void writeOptionalLong(Long l) throws IOException {
        if (l == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeLong(l);
        }
    }

    default void writeOptionalVLong(Long l) throws IOException {
        if (l == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeVLong(l);
        }
    }

    default void writeOptionalZLong(Long l) throws IOException {
        if (l == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeZLong(l);
        }
    }

    /**
     * Writes a 4 byte float.
     */
    default void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Writes a 1-5 byte float with reduced precision.
     * Small positive numbers will be more efficient (1 byte) and small negative numbers will be
     * inefficient (5 bytes).
     */
    default void writeVFloat(float value, float precision) throws IOException {
        writeVInt((int) (value * precision));
    }

    /**
     * Writes a 1-9 byte double with reduced precision.
     * Small positive numbers will be more efficient (1 byte) and small negative numbers will be
     * inefficient (9 bytes).
     */
    default void writeVDouble(double value, double precision, boolean optimizePositive) throws IOException {
        writeVLong((long) (value * precision));
    }

    /**
     * Writes an optional {@link Float}.
     */
    default void writeOptionalFloat(Float v) throws IOException {
        if (v == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeFloat(v);
        }
    }

    /**
     * Writes a 8 byte double.
     */
    default void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes an optional {@link Double}.
     */
    default void writeOptionalDouble(Double v) throws IOException {
        if (v == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeDouble(v);
        }
    }

    byte[] getBuffer();
}
