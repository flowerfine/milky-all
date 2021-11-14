package cn.sliew.milky.property.jackson;

import cn.sliew.milky.property.Mergeable;
import cn.sliew.milky.property.Settings;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wnameless.json.flattener.JsonFlattener;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;
import static cn.sliew.milky.common.check.Ensures.notBlank;

public class JacksonSettings implements Settings<JsonNode> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JacksonSettings fallback;

    private final String name;
    private final JsonNode source;
    private final ObjectMapper objectMapper;

    public JacksonSettings(String name, JsonNode source) {
        this(name, source, OBJECT_MAPPER);
    }

    public JacksonSettings(String name, JsonNode source, ObjectMapper objectMapper) {
        notBlank(name, () -> "Settings name must not be blank");
        checkNotNull(source, () -> "Settings source must not be null");
        checkNotNull(objectMapper, () -> "Settings objectMapper must not be null");
        this.name = name;
        this.source = source;
        this.objectMapper = objectMapper;
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
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(source.fieldNames(), Spliterator.ORDERED), false)
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Override
    public Set<String> getFlattenKeySet() {
        if (source.isObject()) {
            return JsonFlattener.flattenAsMap(source.toString()).keySet();
        }
        return new HashSet<>();
    }

    @Override
    public JsonNode get(String setting) {
        return source.get(setting);
    }

    @Override
    public JsonNode get(String setting, Object defaultValue) {
        return Optional.ofNullable(source.get(setting)).orElse((JsonNode) defaultValue);
    }

    @Override
    public Settings<JsonNode> getByPrefix(String prefix) {
        if (source.isObject()) {
            Map<String, Object> sourceMap = objectMapper.convertValue(source, new TypeReference<>() {});
            FilteredMap filteredMap = new FilteredMap(sourceMap, (k) -> k.startsWith(prefix), prefix);
            return new JacksonSettings(name, objectMapper.valueToTree(filteredMap));
        }
        return this;
    }

    @Override
    public Settings filter(Predicate<String> predicate) {
        if (source.isObject()) {
            ObjectNode objectNode = (ObjectNode) source;
            Set<String> fields = getKeySet().stream().filter(predicate).collect(Collectors.toSet());
            ObjectNode retain = objectNode.retain(fields);
            return new JacksonSettings(name, retain);
        }
        return this;
    }

    @Override
    public List<JsonNode> getAsList(String setting) {
        JsonNode jsonNode = get(setting);
        if (jsonNode.isArray()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.iterator(), Spliterator.ORDERED), false)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, JsonNode> getGroups(String settingsPrefix) {
        JsonNode jsonNode = get(settingsPrefix);
        if (jsonNode.isObject()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.fields(), Spliterator.ORDERED), false)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return Collections.emptyMap();
    }

    @Override
    public Mergeable withFallback(Mergeable fallback) {
        if (fallback instanceof JacksonSettings) {
            this.fallback = (JacksonSettings) fallback;
        }
        return this;
    }
}
