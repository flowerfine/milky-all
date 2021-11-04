package cn.sliew.milky.property.jackson;

import cn.sliew.milky.property.Mergeable;
import cn.sliew.milky.property.Setting;
import cn.sliew.milky.property.Settings;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.function.Predicate;

import static cn.sliew.milky.common.check.Ensures.*;

public class JacksonSettings implements Settings<JsonNode> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String name;
    private final JsonNode source;

    public JacksonSettings(String name, JsonNode source) {
        notBlank(name, () -> "Settings name must not be blank");
        checkNotNull(source, () -> "Settings source must not be null");
        this.name = name;
        this.source = source;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JsonNode getSource() {
        return source;
    }

    @Override
    public boolean isEmpty() {
        return source.isEmpty();
    }

    @Override
    public int size() {
        return source.size();
    }

    @Override
    public Set<String> getKeySet() {
        if (source.isObject()) {
            return OBJECT_MAPPER.convertValue(source, new TypeReference<Map<String, Object>>() {}).keySet();
        }
        return new HashSet<>();
    }

    @Override
    public JsonNode get(String setting) {
        return source.get(setting);
    }

    @Override
    public  JsonNode get(String setting, Object defaultValue) {
        return Optional.ofNullable(source.get(setting)).orElse((JsonNode) defaultValue);
    }

    @Override
    public Settings<JsonNode> getByPrefix(String prefix) {
        return new JacksonSettings(name, get(prefix));
    }

    @Override
    public Settings filter(Predicate<String> predicate) {
        return null;
    }

    @Override
    public List getAsList(String setting) {
        return null;
    }

    @Override
    public Map<String, Settings> getGroups(String settingsPrefix) {
        return null;
    }

    @Override
    public Mergeable withFallback(Mergeable fallback) {
        return null;
    }
}
