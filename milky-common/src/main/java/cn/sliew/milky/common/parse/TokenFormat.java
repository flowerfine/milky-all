package cn.sliew.milky.common.parse;

import cn.sliew.milky.common.util.ToStringBuilder;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static cn.sliew.milky.common.check.Ensures.notBlank;
import static java.util.stream.Collectors.toList;

/**
 * place holder parse util from junit5.
 */
public class TokenFormat implements Serializable {

    private static final long serialVersionUID = 2480921128990881242L;

    private static final TokenFormat DEFAULT_FORMAT = new TokenFormat('[', ':', ']', '/');

    static TokenFormat getDefault() {
        return DEFAULT_FORMAT;
    }

    private static String quote(char c) {
        return Pattern.quote(String.valueOf(c));
    }

    private static String encode(char c) {
        try {
            return URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 should be supported", e);
        }
    }

    private final char openSegment;
    private final char closeSegment;
    private final char segmentDelimiter;
    private final char typeValueSeparator;
    private final Pattern segmentPattern;
    private final Map<Character, String> encodedCharacterMap = new HashMap<>();

    public TokenFormat(char openSegment, char typeValueSeparator, char closeSegment, char segmentDelimiter) {
        this.openSegment = openSegment;
        this.typeValueSeparator = typeValueSeparator;
        this.closeSegment = closeSegment;
        this.segmentDelimiter = segmentDelimiter;
        this.segmentPattern = Pattern.compile(
                String.format("%s(.+)%s(.+)%s", quote(openSegment), quote(typeValueSeparator), quote(closeSegment)),
                Pattern.DOTALL);

        // Compute "forbidden" character encoding map.
        // Note that the map is always empty at this point. Thus the use of
        // computeIfAbsent() is purely syntactic sugar.
        encodedCharacterMap.computeIfAbsent('%', TokenFormat::encode);
        encodedCharacterMap.computeIfAbsent('+', TokenFormat::encode);
        encodedCharacterMap.computeIfAbsent(openSegment, TokenFormat::encode);
        encodedCharacterMap.computeIfAbsent(typeValueSeparator, TokenFormat::encode);
        encodedCharacterMap.computeIfAbsent(closeSegment, TokenFormat::encode);
        encodedCharacterMap.computeIfAbsent(segmentDelimiter, TokenFormat::encode);
    }

    /**
     * Parse a {@code Token} from the supplied string representation.
     *
     * @return a properly constructed {@code Token}
     * @throws TokenParseException if the string cannot be parsed
     */
    Token parse(String source) throws TokenParseException {
        String[] parts = source.split(String.valueOf(this.segmentDelimiter));
        List<Segment> segments = Arrays.stream(parts).map(this::createSegment).collect(toList());
        return new UniqueId(this, segments);
    }


    public static class Segment implements Serializable {

        private static final long serialVersionUID = 4187949052186059932L;

        private final String type;
        private final String value;

        /**
         * Create a new {@code Segment} using the supplied {@code type} and {@code value}.
         *
         * @param type  the type of this segment
         * @param value the value of this segment
         */
        Segment(String type, String value) {
            notBlank(type, "type must not be null or blank");
            notBlank(value, "value must not be null or blank");
            this.type = type;
            this.value = value;
        }

        /**
         * Get the type of this segment.
         */
        public String getType() {
            return this.type;
        }

        /**
         * Get the value of this segment.
         */
        public String getValue() {
            return this.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Segment that = (Segment) o;
            return Objects.equals(this.type, that.type) && Objects.equals(this.value, that.value);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("type", this.type)
                    .append("value", this.value)
                    .toString();
        }

    }


}
