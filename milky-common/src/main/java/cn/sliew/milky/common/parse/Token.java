package cn.sliew.milky.common.parse;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.sliew.milky.common.check.Ensures.*;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

public class Token implements Cloneable, Serializable {

    private static final long serialVersionUID = -485514933964341727L;

    /**
     * Parse a {@code Token} from the supplied string representation using the default format.
     *
     * @param token the string representation to parse; never {@code null} or blank
     * @return a properly constructed {@code Token}
     * @throws TokenParseException if the string cannot be parsed
     */
    public static Token parse(String token) throws TokenParseException {
        notBlank(token, () -> "Token string must not be null or blank");
        return TokenFormat.getDefault().parse(token);
    }

    /**
     * Create a root Token from the supplied {@code segmentType} and
     * {@code value} using the default format.
     *
     * @param segmentType the segment type; never {@code null} or blank
     * @param value       the value; never {@code null} or blank
     */
    public static Token root(String segmentType, String value) {
        return new Token(TokenFormat.getDefault(), new TokenFormat.Segment(segmentType, value));
    }

    private final TokenFormat tokenFormat;
    private final List<TokenFormat.Segment> segments;
    // lazily computed
    private transient int hashCode;
    // lazily computed
    private transient SoftReference<String> toString;

    private Token(TokenFormat tokenFormat, TokenFormat.Segment segment) {
        this(tokenFormat, singletonList(segment));
    }

    /**
     * Initialize a {@code Token} instance.
     *
     * @implNote A defensive copy of the segment list is <b>not</b> created by
     * this implementation. All callers should immediately drop the reference
     * to the list instance that they pass into this constructor.
     */
    Token(TokenFormat tokenFormat, List<TokenFormat.Segment> segments) {
        this.tokenFormat = tokenFormat;
        this.segments = segments;
    }

    final Optional<TokenFormat.Segment> getRoot() {
        return this.segments.isEmpty() ? Optional.empty() : Optional.of(this.segments.get(0));
    }

    /**
     * Get the immutable list of {@linkplain TokenFormat.Segment segments} that make up this
     * {@code Token}.
     */
    public final List<TokenFormat.Segment> getSegments() {
        return unmodifiableList(this.segments);
    }

    /**
     * Construct a new {@code Token} by appending a new {@link TokenFormat.Segment}, based
     * on the supplied {@code segmentType} and {@code value}, to the end of this
     * {@code Token}.
     *
     * <p>This {@code Token} will not be modified.
     *
     * <p>Neither the {@code segmentType} nor the {@code value} may contain any
     * of the special characters used for constructing the string representation
     * of this {@code Token}.
     *
     * @param segmentType the type of the segment; never {@code null} or blank
     * @param value       the value of the segment; never {@code null} or blank
     */
    public final Token append(String segmentType, String value) {
        return append(new TokenFormat.Segment(segmentType, value));
    }

    /**
     * Construct a new {@code Token} by appending a new {@link TokenFormat.Segment} to
     * the end of this {@code Token}.
     *
     * <p>This {@code Token} will not be modified.
     *
     * @param segment the segment to be appended; never {@code null}
     */
    public final Token append(TokenFormat.Segment segment) {
        checkNotNull(segment, () -> "segment must not be null");
        List<TokenFormat.Segment> baseSegments = new ArrayList<>(this.segments.size() + 1);
        baseSegments.addAll(this.segments);
        baseSegments.add(segment);
        return new Token(this.tokenFormat, baseSegments);
    }

    /**
     * Determine if the supplied {@code Token} is a prefix for this
     * {@code Token}.
     *
     * @param potentialPrefix the {@code Token} to be checked; never {@code null}
     */
    public boolean hasPrefix(Token potentialPrefix) {
        checkNotNull(potentialPrefix, () -> "potentialPrefix must not be null");
        int size = this.segments.size();
        int prefixSize = potentialPrefix.segments.size();
        return size >= prefixSize && this.segments.subList(0, prefixSize).equals(potentialPrefix.segments);
    }

    /**
     * Construct a new {@code Token} and removing the last {@link TokenFormat.Segment} of
     * this {@code Token}.
     *
     * <p>This {@code Token} will not be modified.
     *
     * @return a new {@code Token}; never {@code null}
     */
    public Token removeLastSegment() {
        checkState(this.segments.size() > 1, () -> "Cannot remove last remaining segment");
        return new Token(tokenFormat, new ArrayList<>(this.segments.subList(0, this.segments.size() - 1)));
    }

    /**
     * Get the last {@link TokenFormat.Segment} of this {@code Token}.
     *
     * @return the last {@code Segment}; never {@code null}
     */
    public TokenFormat.Segment getLastSegment() {
        return this.segments.get(this.segments.size() - 1);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Token that = (Token) o;
        return this.segments.equals(that.segments);
    }

    @Override
    public int hashCode() {
        int value = this.hashCode;
        if (value == 0) {
            value = this.segments.hashCode();
            if (value == 0) {
                // handle the edge case of the computed hashCode being 0
                value = 1;
            }
            // this is a benign race like String#hash
            // we potentially read and write values from multiple threads
            // without a happens-before relationship
            // however the JMM guarantees us that we only ever see values
            // that were valid at one point, either 0 or the hash code
            // so we might end up not seeing a value that a different thread
            // has computed or multiple threads writing the same value
            this.hashCode = value;
        }
        return value;
    }

    /**
     * Generate the unique, formatted string representation of this {@code Token}
     * using the configured {@link TokenFormat}.
     */
    @Override
    public String toString() {
        SoftReference<String> s = this.toString;
        String value = s == null ? null : s.get();
        if (value == null) {
            value = this.tokenFormat.format(this);
            // this is a benign race like String#hash
            // we potentially read and write values from multiple threads
            // without a happens-before relationship
            // however the JMM guarantees us that we only ever see values
            // that were valid at one point, either null or the toString value
            // so we might end up not seeing a value that a different thread
            // has computed or multiple threads writing the same value
            this.toString = new SoftReference<>(value);
        }
        return value;
    }
}
