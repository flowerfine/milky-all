package cn.sliew.milky.common.settings;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class SettingsTestCase extends MilkyTestCase {

    @Test
    public void testGet() {
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
        assertEquals(Integer.valueOf(1), settings.getAsInt("foo3", null));
        assertEquals(Long.valueOf(2L), settings.getAsLong("foo4", null));
        assertEquals(Float.valueOf(0.1f), settings.getAsFloat("foo5", null));
        assertEquals(Double.valueOf(0.2d), settings.getAsDouble("foo6", null));
        assertNull(settings.get("foo7"));
        assertThat(settings.getAsList("foo8"), contains("1", "2", "3"));
    }

    @Test
    public void testGetAsSettings() {
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

    @Test
    public void testReplacePropertiesPlaceholderSystemProperty() {
        String value = System.getProperty("java.home");
        assertFalse(value.isEmpty());
        Settings settings = Settings.builder()
                .put("property.placeholder", value)
                .put("setting1", "${property.placeholder}")
                .replacePropertyPlaceholders()
                .build();
        assertThat(settings.get("setting1"), equalTo(value));
    }

    @Test
    public void testReplacePropertiesPlaceholderSystemPropertyList() {
        final String hostname = randomAlphaOfLength(16);
        final String hostip = randomAlphaOfLength(16);
        final Settings settings = Settings.builder()
                .putList("setting1", "${HOSTNAME}", "${HOSTIP}")
                .replacePropertyPlaceholders(name -> name.equals("HOSTNAME") ? hostname : name.equals("HOSTIP") ? hostip : null)
                .build();
        assertThat(settings.getAsList("setting1"), contains(hostname, hostip));
    }
//
//    public void testReplacePropertiesPlaceholderSystemVariablesHaveNoEffect() {
//        final String value = System.getProperty("java.home");
//        assertNotNull(value);
//        final IllegalArgumentException e = expectThrows(IllegalArgumentException.class, () -> Settings.builder()
//                .put("setting1", "${java.home}")
//                .replacePropertyPlaceholders()
//                .build());
//        assertThat(e, hasToString(containsString("Could not resolve placeholder 'java.home'")));
//    }
//
//    public void testReplacePropertiesPlaceholderByEnvironmentVariables() {
//        final String hostname = randomAlphaOfLength(16);
//        final Settings implicitEnvSettings = Settings.builder()
//                .put("setting1", "${HOSTNAME}")
//                .replacePropertyPlaceholders(name -> "HOSTNAME".equals(name) ? hostname : null)
//                .build();
//        assertThat(implicitEnvSettings.get("setting1"), equalTo(hostname));
//    }
}
