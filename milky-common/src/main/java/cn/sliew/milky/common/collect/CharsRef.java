package cn.sliew.milky.common.collect;


/**
 * Represents char[], as a slice (offset + length) into an existing char[].
 * The {@link #chars} member should never be null; use
 * {@link #EMPTY_CHARS} if necessary.
 */
public final class CharsRef implements CharSequence {
    /**
     * An empty character array for convenience
     */
    public static final char[] EMPTY_CHARS = new char[0];
    /**
     * The contents of the CharsRef. Should never be {@code null}.
     */
    public char[] chars;
    /**
     * Offset of first valid character.
     */
    public int offset;
    /**
     * Length of used characters.
     */
    public int length;

    /**
     * Creates a new {@link CharsRef} initialized an empty array zero-length
     */
    public CharsRef() {
        this(EMPTY_CHARS, 0, 0);
    }

    /**
     * Creates a new {@link CharsRef} initialized with an array of the given
     * capacity
     */
    public CharsRef(int capacity) {
        chars = new char[capacity];
    }

    /**
     * Creates a new {@link CharsRef} initialized with the given array, offset and
     * length
     */
    public CharsRef(char[] chars, int offset, int length) {
        this.chars = chars;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Creates a new {@link CharsRef} initialized with the given Strings character
     * array
     */
    public CharsRef(String string) {
        this.chars = string.toCharArray();
        this.offset = 0;
        this.length = chars.length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        final int end = offset + length;
        for (int i = offset; i < end; i++) {
            result = prime * result + chars[i];
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof CharsRef) {
            CharsRef that = (CharsRef) other;
            return arrayEquals(this.chars, this.offset, this.offset + this.length,
                    that.chars, that.offset, that.offset + that.length);
        }
        return false;
    }

    /**
     * Behaves like Java 9's Arrays.equals
     */
    private boolean arrayEquals(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {
        checkFromToIndex(aFromIndex, aToIndex, a.length);
        checkFromToIndex(bFromIndex, bToIndex, b.length);
        int aLen = aToIndex - aFromIndex;
        int bLen = bToIndex - bFromIndex;
        // lengths differ: cannot be equal
        if (aLen != bLen) {
            return false;
        }
        for (int i = 0; i < aLen; i++) {
            if (a[i + aFromIndex] != b[i + bFromIndex]) {
                return false;
            }
        }
        return true;
    }

    private void checkFromToIndex(int fromIndex, int toIndex, int length) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex " + fromIndex + " > toIndex " + toIndex);
        }
        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException("Range [" + fromIndex + ", " + toIndex + ") out-of-bounds for length " + length);
        }
    }

    @Override
    public String toString() {
        return new String(chars, offset, length);
    }

    @Override
    public int length() {
        return length;
    }
}