package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.primitives.Integers;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testGetByPrefix() {
        Settings settings = Settings.builder()
                .put("bar", "hello world")
                .put("foo", "abc")
                .put("foo.bar", "def")
                .put("foo.baz", "ghi")
                .build();
        Settings fooSettings1 = settings.getByPrefix("foo.");
        Settings fooSettings2 = settings.getByPrefix("foo");

        assertEquals(2, fooSettings1.size());
        assertEquals(3, fooSettings2.size());
        assertThat(fooSettings1.get("bar"), equalTo("def"));
        assertThat(fooSettings1.get("baz"), equalTo("ghi"));
        assertThat(fooSettings2.get(".bar"), equalTo("def"));
        assertThat(fooSettings2.get(".baz"), equalTo("ghi"));
        assertThat(fooSettings2.get(""), equalTo("abc"));
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
//        final String hostname = randomAlphaOfLength(16);
//        final String hostip = randomAlphaOfLength(16);
//        final Settings settings = Settings.builder()
//                .putList("setting1", "${HOSTNAME}", "${HOSTIP}")
//                .replacePropertyPlaceholders(name -> name.equals("HOSTNAME") ? hostname : name.equals("HOSTIP") ? hostip : null)
//                .build();
//        assertThat(settings.getAsList("setting1"), contains(hostname, hostip));
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

    @Test
    public void testSimpleUpdate() {

    }

    @Test
    public void testListSetting() {
        Settings settings = Settings.builder()
                .putList("foo.bar", "1", "2", "3")
                .build();
        Setting<List<Integer>> listSetting = SettingHelper.listSetting("foo.bar", Collections.emptyList(), Integers::parseInteger);
        assertTrue(listSetting.isListSetting());

        List<Integer> integers = listSetting.get(settings);
        assertThat(integers, contains(1, 2, 3));
    }

    @Test
    public void testGroupSetting() {
        Settings settings = Settings.builder()
                .put("foo.bar.1.value", "1")
                .put("foo.bar.2.value", "2")
                .put("foo.bar.3.value", "3")
                .build();
        Setting<Settings> setting = SettingHelper.groupSetting("foo.bar.");
        assertTrue(setting.isGroupSetting());
    }

    @Test
    public void testAffixSetting() {

    }

    public void testAffixKeySetting() {
        AffixSetting<Boolean> setting =
                SettingHelper.affixKeySetting("foo.", "enable", (key) -> SettingHelper.boolSetting(key, false));

        AffixKey rawKey = (AffixKey) setting.getRawKey();
        String concreteString = rawKey.getConcreteString("foo.bar.enable");
        String namespace = rawKey.getNamespace("foo.bar.enable");
        SimpleKey bar = rawKey.toConcreteKey("bar");

        assertTrue(setting.match("foo.bar.enable"));
        assertTrue(setting.match("foo.baz.enable"));

        assertFalse(setting.match("foo.bar.baz.enable"));
        assertFalse(setting.match("foo.bar"));
        assertFalse(setting.match("foo.bar.baz.enabled"));
        assertFalse(setting.match("foo"));

        Setting<Boolean> concreteSetting = setting.getConcreteSetting("foo.bar.enable");
        assertTrue(concreteSetting.get(Settings.builder().put("foo.bar.enable", "true").build()));
        assertFalse(concreteSetting.get(Settings.builder().put("foo.baz.enable", "true").build()));

        IllegalArgumentException exc = expectThrows(IllegalArgumentException.class, () -> setting.getConcreteSetting("foo"));
        assertEquals("key [foo] must match [foo.*.enable] but didn't.", exc.getMessage());

        exc = expectThrows(IllegalArgumentException.class, () -> SettingHelper.affixKeySetting("foo", "enable",
                (key) -> SettingHelper.boolSetting(key, false)));
        assertEquals("prefix must end with a '.'", exc.getMessage());

        Setting<List<String>> listAffixSetting = SettingHelper.affixKeySetting("foo.", "bar",
                (key) -> SettingHelper.listSetting(key, Collections.emptyList(), Function.identity()));

        assertTrue(listAffixSetting.match("foo.test.bar"));
        assertTrue(listAffixSetting.match("foo.test_1.bar"));
        assertFalse(listAffixSetting.match("foo.buzz.baz.bar"));
        assertFalse(listAffixSetting.match("foo.bar"));
        assertFalse(listAffixSetting.match("foo.baz"));
        assertFalse(listAffixSetting.match("foo"));
    }

    public void testAffixSettingNamespaces() {
        AffixSetting<Boolean> setting =
                SettingHelper.affixKeySetting("foo.", "enable", (key) -> SettingHelper.boolSetting(key, false));
        Settings build = Settings.builder()
                .put("foo.bar.enable", "true")
                .put("foo.baz.enable", "true")
                .put("foo.boom.enable", "true")
                .put("something.else", "true")
                .build();

        Set<String> namespaces = setting.getNamespaces(build);
        assertEquals(3, namespaces.size());
        assertTrue(namespaces.contains("bar"));
        assertTrue(namespaces.contains("baz"));
        assertTrue(namespaces.contains("boom"));
    }

    public void testAffixAsMap() {
        AffixSetting<String> setting = SettingHelper.prefixKeySetting("foo.bar.", key -> SettingHelper.simpleString(key));
        Settings settings = Settings.builder()
                .put("foo.bar.baz", 2)
                .put("foo.bar.foobar", 3)
                .build();
        Map<String, String> asMap = setting.getAsMap(settings);
        assertEquals(2, asMap.size());
        assertEquals("2", asMap.get("baz"));
        assertEquals("3", asMap.get("foobar"));

        setting = SettingHelper.prefixKeySetting("foo.bar.", key -> SettingHelper.simpleString(key));
        settings = Settings.builder()
                .put("foo.bar.baz", 2)
                .put("foo.bar.foobar", 3)
                .put("foo.bar.baz.deep", 45)
                .build();
        asMap = setting.getAsMap(settings);
        assertEquals(3, asMap.size());
        assertEquals("2", asMap.get("baz"));
        assertEquals("3", asMap.get("foobar"));
        assertEquals("45", asMap.get("baz.deep"));
    }

    public void testGetAllConcreteSettings() {
        AffixSetting<List<String>> listAffixSetting = SettingHelper.affixKeySetting("foo.", "bar",
                (key) -> SettingHelper.listSetting(key, Collections.emptyList(), Function.identity()));

        Settings settings = Settings.builder()
                .putList("foo.1.bar", "1", "2")
                .putList("foo.2.bar", "3", "4", "5")
                .putList("foo.bar", "6")
                .putList("some.other", "6")
                .putList("foo.3.bar", "6")
                .build();
        Stream<Setting<List<String>>> allConcreteSettings = listAffixSetting.getAllConcreteSettings(settings);
        Map<String, List<String>> collect = allConcreteSettings.collect(Collectors.toMap(Setting::getKey, (s) -> s.get(settings)));
        assertEquals(3, collect.size());
        assertEquals(Arrays.asList("1", "2"), collect.get("foo.1.bar"));
        assertEquals(Arrays.asList("3", "4", "5"), collect.get("foo.2.bar"));
        assertEquals(Arrays.asList("6"), collect.get("foo.3.bar"));
    }

    public void testAffixSettingsFailOnGet() {
        AffixSetting<List<String>> listAffixSetting = SettingHelper.affixKeySetting("foo.", "bar",
                (key) -> SettingHelper.listSetting(key, Collections.singletonList("testelement"), Function.identity()));

        expectThrows(UnsupportedOperationException.class, () -> listAffixSetting.get(Settings.EMPTY));
        assertEquals(Collections.singletonList("testelement"), listAffixSetting.getDefault(Settings.EMPTY));
        assertEquals("[\"testelement\"]", listAffixSetting.getDefaultRaw(Settings.EMPTY));
    }

    public void testAffixSettingsValidatorDependencies() {
        Setting<Integer> affix = SettingHelper.affixKeySetting("abc.", "def", k -> SettingHelper.intSetting(k, 10));
        Setting<Integer> fix0 = SettingHelper.intSetting("abc.tuv", 20, 0);
        Setting<Integer> fix1 = SettingHelper.intSetting("abc.qrx", 20, 0, new Validator<Integer>() {

            @Override
            public void validate(Integer value) {}

            String toString(Map<Setting<Integer>, Integer> s) {
                return s.entrySet().stream()
                        .map(e -> e.getKey().getKey() + ":" + e.getValue().toString())
                        .sorted()
                        .collect(Collectors.joining(","));
            }

            @Override
            public void validate(Integer value, Map<Setting<Integer>, Integer> settings) {
                if (settings.get(fix0).equals(fix0.getDefault(Settings.EMPTY))) {
                    settings.remove(fix0);
                }
                if (settings.size() == 1) {
                    throw new IllegalArgumentException(toString(settings));
                } else if (settings.size() == 2) {
                    throw new IllegalArgumentException(toString(settings));
                }
            }

            @Override
            public Iterator<Setting<Integer>> settings() {
                List<Setting<Integer>> a = Arrays.asList(affix, fix0);
                return a.iterator();
            }
        });

        IllegalArgumentException e = expectThrows(IllegalArgumentException.class,
                () -> fix1.get(Settings.builder().put("abc.1.def", 11).put("abc.2.def", 12).put("abc.qrx", 11).build()));
        assertThat(e.getMessage(), is("abc.1.def:11,abc.2.def:12"));

        e = expectThrows(IllegalArgumentException.class,
                () -> fix1.get(Settings.builder().put("abc.3.def", 13).put("abc.qrx", 20).build()));
        assertThat(e.getMessage(), is("abc.3.def:13"));

        e = expectThrows(IllegalArgumentException.class,
                () -> fix1.get(Settings.builder().put("abc.4.def", 14).put("abc.qrx", 20).put("abc.tuv", 50).build()));
        assertThat(e.getMessage(), is("abc.4.def:14,abc.tuv:50"));

        assertEquals(
                fix1.get(Settings.builder()
                        .put("abc.3.def", 13).put("abc.1.def", 11).put("abc.2.def", 12).put("abc.qrx", 20)
                        .build()),
                Integer.valueOf(20)
        );

        assertEquals(fix1.get(Settings.builder().put("abc.qrx", 30).build()), Integer.valueOf(30));
    }
}
