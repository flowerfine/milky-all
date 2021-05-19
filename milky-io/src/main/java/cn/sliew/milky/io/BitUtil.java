package cn.sliew.milky.io;

/**
 * A variety of high efficiency bit twiddling routines.
 */
public final class BitUtil {

    /**
     * Same as {@link #zigZagEncode(long)} but on integers.
     */
    public static int zigZagEncode(int i) {
        return (i >> 31) ^ (i << 1);
    }

    /**
     * <a href="https://developers.google.com/protocol-buffers/docs/encoding#types">Zig-zag</a>
     * encode the provided long. Assuming the input is a signed long whose
     * absolute value can be stored on <tt>n</tt> bits, the returned value will
     * be an unsigned long that can be stored on <tt>n+1</tt> bits.
     */
    public static long zigZagEncode(long l) {
        return (l >> 63) ^ (l << 1);
    }

    /**
     * Decode an int previously encoded with {@link #zigZagEncode(int)}.
     */
    public static int zigZagDecode(int i) {
        return ((i >>> 1) ^ -(i & 1));
    }

    /**
     * Decode a long previously encoded with {@link #zigZagEncode(long)}.
     */
    public static long zigZagDecode(long l) {
        return ((l >>> 1) ^ -(l & 1));
    }

    private BitUtil() {
        throw new IllegalStateException("no instance");
    }
}
