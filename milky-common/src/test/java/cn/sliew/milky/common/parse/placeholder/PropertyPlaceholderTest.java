package cn.sliew.milky.common.parse.placeholder;

import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class PropertyPlaceholderTest extends MilkyTestCase {

    private static final Logger log = LoggerFactory.getLogger(PropertyPlaceholderTest.class);

    @Test
    void testSimple() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "{", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("bar1", propertyPlaceholder.replacePlaceholders("{foo1}", placeholderResolver));
        assertEquals("a bar1b", propertyPlaceholder.replacePlaceholders("a {foo1}b", placeholderResolver));
        assertEquals("bar1bar2", propertyPlaceholder.replacePlaceholders("{foo1}{foo2}", placeholderResolver));
        assertEquals("a bar1 b bar2 c", propertyPlaceholder.replacePlaceholders("a {foo1} b {foo2} c", placeholderResolver));
    }

    @Test
    void testVariousPrefixSuffix() {
        // Test various prefix/suffix lengths
        PropertyPlaceholder ppEqualsPrefix = new PropertyPlaceholder(log, "{", "}", ":", false);
        PropertyPlaceholder ppLongerPrefix = new PropertyPlaceholder(log, "${", "}", ":", false);
        PropertyPlaceholder ppShorterPrefix = new PropertyPlaceholder(log, "{", "}}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo", "bar");
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("bar", ppEqualsPrefix.replacePlaceholders("{foo}", placeholderResolver));
        assertEquals("bar", ppLongerPrefix.replacePlaceholders("${foo}", placeholderResolver));
        assertEquals("bar", ppShorterPrefix.replacePlaceholders("{foo}}", placeholderResolver));
    }

    @Test
    void testDefaultValue() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("bar", propertyPlaceholder.replacePlaceholders("${foo:bar}", placeholderResolver));
        assertEquals("", propertyPlaceholder.replacePlaceholders("${foo:}", placeholderResolver));
    }

    @Test
    void testIgnoredUnresolvedPlaceholder() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", true);
        Map<String, String> map = new LinkedHashMap<>();
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("${foo}", propertyPlaceholder.replacePlaceholders("${foo}", placeholderResolver));
    }

    @Test
    void testNotIgnoredUnresolvedPlaceholder() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        try {
            propertyPlaceholder.replacePlaceholders("${foo}", placeholderResolver);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Could not resolve placeholder 'foo' in value \"${foo}\""));
        }
    }

    @Test
    void testShouldIgnoreMissing() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, true, true);
        assertEquals("bar", propertyPlaceholder.replacePlaceholders("bar${foo}", placeholderResolver));
    }

    @Test
    void testRecursive() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo", "${foo1}");
        map.put("foo1", "${foo2}");
        map.put("foo2", "bar");
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("bar", propertyPlaceholder.replacePlaceholders("${foo}", placeholderResolver));
        assertEquals("abarb", propertyPlaceholder.replacePlaceholders("a${foo}b", placeholderResolver));
    }

    @Test
    void testNestedLongerPrefix() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo", "${foo1}");
        map.put("foo1", "${foo2}");
        map.put("foo2", "bar");
        map.put("barbar", "baz");
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("baz", propertyPlaceholder.replacePlaceholders("${bar${foo}}", placeholderResolver));
    }

    @Test
    void testNestedSameLengthPrefixSuffix() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "{", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo", "{foo1}");
        map.put("foo1", "{foo2}");
        map.put("foo2", "bar");
        map.put("barbar", "baz");
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("baz", propertyPlaceholder.replacePlaceholders("{bar{foo}}", placeholderResolver));
    }

    @Test
    void testNestedShorterPrefix() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "{", "}}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo", "{foo1}}");
        map.put("foo1", "{foo2}}");
        map.put("foo2", "bar");
        map.put("barbar", "baz");
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        assertEquals("baz", propertyPlaceholder.replacePlaceholders("{bar{foo}}}}", placeholderResolver));
    }

    @Test
    void testCircularReference() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("foo", "${bar}");
        map.put("bar", "${foo}");
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, false, true);
        try {
            propertyPlaceholder.replacePlaceholders("${foo}", placeholderResolver);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Circular placeholder reference 'foo' in property definitions"));
        }
    }

    @Test
    void testNotRemoveMissing() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder(log, "${", "}", ":", false);
        Map<String, String> map = new LinkedHashMap<>();
        PropertyPlaceholder.PlaceholderResolver placeholderResolver = new SimplePlaceholderResolver(map, true, false);
        assertEquals("bar${foo}", propertyPlaceholder.replacePlaceholders("bar${foo}", placeholderResolver));
    }

    private class SimplePlaceholderResolver implements PropertyPlaceholder.PlaceholderResolver {
        private Map<String, String> map;
        private boolean shouldIgnoreMissing;
        private boolean shouldRemoveMissing;

        SimplePlaceholderResolver(Map<String, String> map, boolean shouldIgnoreMissing, boolean shouldRemoveMissing) {
            this.map = map;
            this.shouldIgnoreMissing = shouldIgnoreMissing;
            this.shouldRemoveMissing = shouldRemoveMissing;
        }

        @Override
        public Optional<String> resolvePlaceholder(String placeholderName) {
            return Optional.ofNullable(map.get(placeholderName));
        }

        @Override
        public boolean shouldIgnoreMissing(String placeholderName) {
            return shouldIgnoreMissing;
        }

        @Override
        public boolean shouldRemoveMissingPlaceholder(String placeholderName) {
            return shouldRemoveMissing;
        }
    }
}
