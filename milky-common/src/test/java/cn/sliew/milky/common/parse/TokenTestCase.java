package cn.sliew.milky.common.parse;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenTestCase extends MilkyTestCase {

    @Test
    public void createTokenFromTypeAndValue() {
        Token token = Token.root("type", "value");
        assertEquals("[type:value]", token.toString());
        assertSegment(token.getSegments().get(0), "type", "value");
    }

    @Test
    void rootSegmentCanBeRetrieved() {
        Token token = Token.root("type", "value");
        assertThat(token.getRoot()).contains(new TokenFormat.Segment("type", "value"));
    }

    @Test
    void appendingOneSegment() {
        Token root = Token.root("root", "root");

        assertThat(root.getSegments()).hasSize(1);
        assertSegment(root.getSegments().get(0), "root", "root");

        Token classId = root.append("class", "cn.sliew.milky.common.parse.TokenTestCase");

        assertThat(classId.getSegments()).hasSize(2);
        assertSegment(classId.getSegments().get(0), "root", "root");
        assertSegment(classId.getSegments().get(1), "class", "cn.sliew.milky.common.parse.TokenTestCase");
    }

    @Test
    void appendingSeveralSegments() {
        Token root = Token.root("root", "root");
        Token token = root.append("t1", "v1").append("t2", "v2").append("t3", "v3");

        assertThat(token.getSegments()).hasSize(4);
        assertSegment(token.getSegments().get(0), "root", "root");
        assertSegment(token.getSegments().get(1), "t1", "v1");
        assertSegment(token.getSegments().get(2), "t2", "v2");
        assertSegment(token.getSegments().get(3), "t3", "v3");
    }

    @Test
    void appendingSegmentInstance() {
        Token token = Token.root("root", "root").append("t1", "v1");
        token = token.append(new TokenFormat.Segment("t2", "v2"));

        assertThat(token.getSegments()).hasSize(3);
        assertSegment(token.getSegments().get(0), "root", "root");
        assertSegment(token.getSegments().get(1), "t1", "v1");
        assertSegment(token.getSegments().get(2), "t2", "v2");
    }

    @Test
    void appendingNullIsNotAllowed() {
        Token token = Token.root("root", "root");

        assertThrows(NullPointerException.class, () -> token.append(null));
        assertThrows(IllegalStateException.class, () -> token.append(null, "foo"));
        assertThrows(IllegalStateException.class, () -> token.append("foo", null));
    }

    private void assertSegment(TokenFormat.Segment segment, String expectedType, String expectedValue) {
        assertEquals(expectedType, segment.getType(), "segment type");
        assertEquals(expectedValue, segment.getValue(), "segment value");
    }
}
