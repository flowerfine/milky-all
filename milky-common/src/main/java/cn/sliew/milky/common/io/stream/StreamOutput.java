package cn.sliew.milky.common.io.stream;

import cn.sliew.milky.common.util.BitUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A stream from another node to this node. Technically, it can also be streamed from a byte array but that is mostly for testing.
 * <p>
 * This class's methods are optimized so you can put the methods that read and write a class next to each other and you can scan them
 * visually for differences. That means that most variables should be read and written in a single line so even large objects fit both
 * reading and writing on the screen. It also means that the methods on this class are named very similarly to {@link StreamInput}. Finally
 * it means that the "barrier to entry" for adding new methods to this class is relatively low even though it is a shared class with code
 * everywhere. That being said, this class deals primarily with {@code List}s rather than Arrays. For the most part calls should adapt to
 * lists, either by storing {@code List}s internally or just converting to and from a {@code List} when calling. This comment is repeated
 * on {@link StreamInput}.
 */
public abstract class StreamOutput extends OutputStream {

    private static final ThreadLocal<byte[]> scratch = ThreadLocal.withInitial(() -> new byte[1024]);

    private static byte ZERO = 0;
    private static byte ONE = 1;
    private static byte TWO = 2;

    private static final Map<Class<?>, Writable.Writer> WRITERS;

    static {
        Map<Class<?>, Writable.Writer> writers = new HashMap<>();
        writers.put(Byte.class, (o, v) -> {
            o.writeByte((byte) 0);
            o.writeByte((Byte) v);
        });
        writers.put(Short.class, (o, v) -> {
            o.writeByte((byte) 1);
            o.writeShort((Short) v);
        });
        writers.put(Integer.class, (o, v) -> {
            o.writeByte((byte) 2);
            o.writeInt((Integer) v);
        });
        writers.put(Long.class, (o, v) -> {
            o.writeByte((byte) 3);
            o.writeLong((Long) v);
        });
        writers.put(Float.class, (o, v) -> {
            o.writeByte((byte) 4);
            o.writeFloat((float) v);
        });
        writers.put(Double.class, (o, v) -> {
            o.writeByte((byte) 5);
            o.writeDouble((double) v);
        });
        writers.put(Boolean.class, (o, v) -> {
            o.writeByte((byte) 6);
            o.writeBoolean((boolean) v);
        });
        WRITERS = Collections.unmodifiableMap(writers);
    }

    /**
     * Closes this stream to further operations.
     */
    @Override
    public abstract void close() throws IOException;

    /**
     * Forces any buffered output to be written.
     */
    @Override
    public abstract void flush() throws IOException;

    public abstract void reset() throws IOException;

    /**
     * Writes a single byte.
     */
    public abstract void writeByte(byte b) throws IOException;

    /**
     * Writes an array of bytes.
     *
     * @param b the bytes to write
     */
    public void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }

    /**
     * Writes an array of bytes.
     *
     * @param b      the bytes to write
     * @param length the number of bytes to write
     */
    public void writeBytes(byte[] b, int length) throws IOException {
        writeBytes(b, 0, length);
    }

    /**
     * Writes an array of bytes.
     *
     * @param b      the bytes to write
     * @param offset the offset in the byte array
     * @param length the number of bytes to write
     */
    public abstract void writeBytes(byte[] b, int offset, int length) throws IOException;

    public final void writeShort(short v) throws IOException {
        final byte[] buffer = scratch.get();
        buffer[0] = (byte) (v >> 8);
        buffer[1] = (byte) v;
        writeBytes(buffer, 0, 2);
    }

    /**
     * Writes an int as four bytes.
     */
    public void writeInt(int i) throws IOException {
        final byte[] buffer = scratch.get();
        buffer[0] = (byte) (i >> 24);
        buffer[1] = (byte) (i >> 16);
        buffer[2] = (byte) (i >> 8);
        buffer[3] = (byte) i;
        writeBytes(buffer, 0, 4);
    }

    /**
     * Writes an optional {@link Integer}.
     */
    public void writeOptionalInt(Integer integer) throws IOException {
        if (integer == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeInt(integer);
        }
    }

    /**
     * Writes an int in a variable-length format.  Writes between one and
     * five bytes.  Smaller values take fewer bytes.  Negative numbers
     * will always use all 5 bytes and are therefore better serialized
     * using {@link #writeInt}
     */
    public void writeVInt(int i) throws IOException {
        /*
         * Shortcut writing single byte because it is very, very common and
         * can skip grabbing the scratch buffer. This is marginally slower
         * than hand unrolling the entire encoding loop but hand unrolling
         * the encoding loop blows out the method size so it can't be inlined.
         * In that case benchmarks of the method itself are faster but
         * benchmarks of methods that use this method are slower.
         * This is philosophically in line with vint in general - it biases
         * twoards being simple and fast for smaller numbers.
         */
        if (Integer.numberOfLeadingZeros(i) >= 25) {
            writeByte((byte) i);
            return;
        }
        byte[] buffer = scratch.get();
        int index = 0;
        do {
            buffer[index++] = ((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        } while ((i & ~0x7F) != 0);
        buffer[index++] = ((byte) i);
        writeBytes(buffer, 0, index);
    }

    public void writeOptionalVInt(Integer integer) throws IOException {
        if (integer == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeVInt(integer);
        }
    }

    /**
     * Writes a long as eight bytes.
     */
    public void writeLong(long i) throws IOException {
        final byte[] buffer = scratch.get();
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

    public void writeOptionalLong(Long l) throws IOException {
        if (l == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeLong(l);
        }
    }

    /**
     * Writes a non-negative long in a variable-length format. Writes between one and ten bytes. Smaller values take fewer bytes. Negative
     * numbers use ten bytes and trip assertions (if running in tests) so prefer {@link #writeLong(long)} or {@link #writeZLong(long)} for
     * negative numbers.
     */
    public void writeVLong(long i) throws IOException {
        if (i < 0) {
            throw new IllegalStateException("Negative longs unsupported, use writeLong or writeZLong for negative numbers [" + i + "]");
        }
        writeVLongNoCheck(i);
    }

    public void writeOptionalVLong(Long l) throws IOException {
        if (l == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeVLong(l);
        }
    }

    /**
     * Writes a long in a variable-length format. Writes between one and ten bytes.
     * Values are remapped by sliding the sign bit into the lsb and then encoded as an unsigned number
     * e.g., 0 -;&gt; 0, -1 -;&gt; 1, 1 -;&gt; 2, ..., Long.MIN_VALUE -;&gt; -1, Long.MAX_VALUE -;&gt; -2
     * Numbers with small absolute value will have a small encoding
     * If the numbers are known to be non-negative, use {@link #writeVLong(long)}
     */
    public void writeZLong(long i) throws IOException {
        final byte[] buffer = scratch.get();
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
     * Writes a long in a variable-length format without first checking if it is negative. Package private for testing. Use
     * {@link #writeVLong(long)} instead.
     */
    void writeVLongNoCheck(long i) throws IOException {
        final byte[] buffer = scratch.get();
        int index = 0;
        while ((i & ~0x7F) != 0) {
            buffer[index++] = ((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        }
        buffer[index++] = ((byte) i);
        writeBytes(buffer, 0, index);
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeOptionalFloat(Float floatValue) throws IOException {
        if (floatValue == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeFloat(floatValue);
        }
    }

    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeOptionalDouble(Double v) throws IOException {
        if (v == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeDouble(v);
        }
    }

    public void writeBoolean(boolean b) throws IOException {
        writeByte(b ? ONE : ZERO);
    }

    public void writeOptionalBoolean(Boolean b) throws IOException {
        if (b == null) {
            writeByte(TWO);
        } else {
            writeBoolean(b);
        }
    }

    @Override
    public void write(int b) throws IOException {
        writeByte((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeBytes(b, off, len);
    }

    /**
     * Notice: when serialization a map, the stream out map with the stream in map maybe have the
     * different key-value orders, they will maybe have different stream order.
     * If want to keep stream out map and stream in map have the same stream order when stream,
     * can use {@code writeMapWithConsistentOrder}
     */
    public void writeGenericValue(Object value) throws IOException {
        if (value == null) {
            writeByte((byte) -1);
            return;
        }
        final Writable.Writer writer = WRITERS.get(value.getClass());
        if (writer != null) {
            writer.write(this, value);
        } else {
            throw new IllegalArgumentException("can not write type [" + value.getClass() + "]");
        }
    }
}