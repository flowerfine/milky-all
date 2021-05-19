package cn.sliew.milky.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

public interface DataInputView extends DataInput, Serializable {

    void readBytes(byte[] b) throws IOException;

    void readBytes(byte[] b, int offset, int len) throws IOException;

    @Override
    default void readFully(byte[] b) throws IOException {
        readBytes(b);
    }

    @Override
    default void readFully(byte[] b, int off, int len) throws IOException {
        readBytes(b, off, len);
    }

    /**
     * Reads a 1 byte boolean.
     */
    @Override
    default boolean readBoolean() throws IOException {
        final byte value = readByte();
        if (value == DataOutputView.ZERO) {
            return false;
        } else if (value == DataOutputView.ONE) {
            return true;
        } else {
            final String message = String.format(Locale.ROOT, "unexpected byte [0x%02x]", value);
            throw new IllegalStateException(message);
        }
    }

    /**
     * Reads an optional {@link Boolean}.
     */
    default Boolean readOptionalBoolean() throws IOException {
        final byte value = readByte();
        if (value == DataOutputView.TWO) {
            return null;
        }

        if (value == DataOutputView.ZERO) {
            return false;
        } else if (value == DataOutputView.ONE) {
            return true;
        } else {
            final String message = String.format(Locale.ROOT, "unexpected byte [0x%02x]", value);
            throw new IllegalStateException(message);
        }
    }

    /**
     * Reads a 2 byte short.
     */
    @Override
    default short readShort() throws IOException {
        return (short) (((readByte() & 0xFF) << 8) | (readByte() & 0xFF));
    }

    /**
     * Reads a 2 byte short as an int from 0 to 65535.
     */
    @Override
    default int readUnsignedShort() throws IOException {
        return ((readByte() & 0xFF) << 8) | (readByte() & 0xFF);
    }

    /**
     * Reads a 2 byte char.
     */
    @Override
    default char readChar() throws IOException {
        return (char) (((readByte() & 0xFF)) << 8 | (readByte() & 0xFF));
    }

    /**
     * Reads a 4 byte int.
     */
    @Override
    default int readInt() throws IOException {
        return ((readByte() & 0xFF) << 24) |
                ((readByte() & 0xFF) << 16) |
                ((readByte() & 0xFF) << 8) |
                (readByte() & 0xFF);
    }

    /**
     * Reads an int stored in variable-length format.
     * Reads between one and five bytes.  Smaller values take fewer bytes.
     * Negative numbers will always use all 5 bytes and are therefore better
     * serialized using {@link #readInt} or {@link #readZInt}
     */
    default int readVInt() throws IOException {
        byte b = readByte();
        int i = b & 0x7F;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7F) << 7;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7F) << 14;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7F) << 21;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        if ((b & 0x80) != 0) {
            throw new IOException("Invalid vInt ((" + Integer.toHexString(b) + " & 0x7f) << 28) | " + Integer.toHexString(i));
        }
        return i | ((b & 0x7F) << 28);
    }

    /**
     * Reads a {@link BitUtil#zigZagDecode(int) zig-zag}-encoded {@link #readVInt() variable-length} integer.
     *
     * @see DataOutputView#writeZInt(int)
     */
    default int readZInt() throws IOException {
        return BitUtil.zigZagDecode(readVInt());
    }

    /**
     * Reads an optional {@link Integer}.
     */
    default Integer readOptionalInt() throws IOException {
        if (readBoolean()) {
            return readInt();
        }
        return null;
    }

    default Integer readOptionalVInt() throws IOException {
        if (readBoolean()) {
            return readVInt();
        }
        return null;
    }

    default Integer readOptionalZInt() throws IOException {
        if (readBoolean()) {
            return readZInt();
        }
        return null;
    }

    /**
     * Reads an 8 byte long.
     */
    default long readLong() throws IOException {
        return (((long) readInt()) << 32) | (readInt() & 0xFFFFFFFFL);
    }

    /**
     * Reads a long stored in variable-length format.
     * Reads between one and ten bytes. Smaller values take fewer bytes.
     * Negative numbers are encoded in ten bytes so prefer {@link #readLong()} or {@link #readZLong()}
     * for negative numbers.
     */
    default long readVLong() throws IOException {
        byte b = readByte();
        long i = b & 0x7FL;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 7;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 14;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 21;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 28;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 35;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 42;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= (b & 0x7FL) << 49;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        i |= ((b & 0x7FL) << 56);
        if ((b & 0x80) == 0) {
            return i;
        }
        b = readByte();
        if (b != 0 && b != 1) {
            throw new IOException("Invalid vlong (" + Integer.toHexString(b) + " << 63) | " + Long.toHexString(i));
        }
        i |= ((long) b) << 63;
        return i;
    }

    default long readZLong() throws IOException {
        long accumulator = 0L;
        int i = 0;
        long currentByte;
        while (((currentByte = readByte()) & 0x80L) != 0) {
            accumulator |= (currentByte & 0x7F) << i;
            i += 7;
            if (i > 63) {
                throw new IOException("variable-length stream is too long");
            }
        }
        return BitUtil.zigZagDecode(accumulator | (currentByte << i));
    }

    /**
     * Reads an optional {@link Long}.
     */
    default Long readOptionalLong() throws IOException {
        if (readBoolean()) {
            return readLong();
        }
        return null;
    }

    default Long readOptionalVLong() throws IOException {
        if (readBoolean()) {
            return readVLong();
        }
        return null;
    }

    default Long readOptionalZLong() throws IOException {
        if (readBoolean()) {
            return readZLong();
        }
        return null;
    }

    /**
     * Reads a 4 byte float.
     */
    default float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a 1-5 byte float with reduced precision.
     */
    default float readVFloat(float precision, boolean optimizePositive) throws IOException {
        return readVInt() / precision;
    }

    /**
     * Reads a 1-9 byte double with reduced precision.
     */
    default double readVarDouble(double precision) throws IOException {
        return readVLong() / precision;
    }

    default Float readOptionalFloat() throws IOException {
        if (readBoolean()) {
            return readFloat();
        }
        return null;
    }

    /**
     * Reads an 8 byte double.
     */
    default double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    default Double readOptionalDouble() throws IOException {
        if (readBoolean()) {
            return readDouble();
        }
        return null;
    }
}
