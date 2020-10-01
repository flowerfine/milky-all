package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.CommonTestCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class SettingsTestCase extends CommonTestCase {


    @Test
    public void testGet() throws Exception {
        Settings settings = Settings.builder()
                .put("foo1", "abc")
                .put("foo2", true)
                .put("foo3", 1)
                .put("foo4", 2L)
                .put("foo5", 0.1f)
                .put("foo6", 0.2)
                .putNull("foo7")
                .putList("foo8", "1", "2", "3")
                .build();
        assertFalse(settings.isEmpty());
        assertEquals(8, settings.size());
        assertTrue(settings.hasValue("foo1"));
        assertFalse(settings.hasValue("bar"));

        assertEquals("abc", settings.get("foo1"));
        assertEquals(true, settings.getAsBoolean("foo2", null));
        assertEquals(1, settings.getAsInt("foo3", null));
        assertEquals(2L, settings.getAsLong("foo4", null));
        assertEquals(0.1f, settings.getAsFloat("foo5", null));
        assertEquals(0.2, settings.getAsDouble("foo6", null));
        assertNull(settings.get("foo7"));
        assertLinesMatch(Arrays.asList("1", "2", "3"), settings.getAsList("foo8"));
    }

    @Test
    public void testGetAsSettings() throws Exception {
        Settings settings = Settings.builder()
                .put("bar", "hello world")
                .put("foo", "abc")
                .put("foo.bar", "def")
                .put("foo.baz", "ghi")
                .build();

        Settings fooSettings = settings.getAsSettings("foo");
        assertFalse(fooSettings.isEmpty());
        assertEquals(2, fooSettings.size());

        assertThat(fooSettings.get("bar"), equalTo("def"));
        assertThat(fooSettings.get("baz"), equalTo("ghi"));
    }
}
