package cn.sliew.milky.common.util;

import cn.sliew.milky.common.exception.Rethrower;
import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.*;

/**
 * jackson utility class.
 */
public class JacksonUtil {

    private static final Logger log = LoggerFactory.getLogger(JacksonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JavaPropsMapper PROPS_MAPPER = new JavaPropsMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private JacksonUtil() {
        throw new AssertionError("No instances intended");
    }

    /**
     * serialize object to json string.
     */
    public static String toJsonString(Object object) {
        return toJsonString(OBJECT_MAPPER, object);
    }

    /**
     * serialize object to json string.
     */
    public static String toJsonString(ObjectMapper mapper, Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("json 序列化失败 object: {}", object, e);
            Rethrower.throwAs(e);
            return null;
        }
    }

    /**
     * deserialize json string to target specified by {@link Class}.
     */
    public static <T> T parseJsonString(String json, Class<T> clazz) {
        return parseJsonString(OBJECT_MAPPER, json, clazz);
    }

    /**
     * deserialize json string to target specified by {@link Class}.
     */
    public static <T> T parseJsonString(ObjectMapper mapper, String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("json 反序列化失败 clazz: {}, json: {}", clazz.getName(), json, e);
            Rethrower.throwAs(e);
            return null;
        }
    }

    /**
     * deserialize json string to target specified by {@link TypeReference}.
     * {@link TypeReference} indicate type generics.
     */
    public static <T> T parseJsonString(String json, TypeReference<T> reference) {
        return parseJsonString(OBJECT_MAPPER, json, reference);
    }

    /**
     * deserialize json string to target specified by {@link TypeReference}.
     * {@link TypeReference} indicate type generics.
     */
    public static <T> T parseJsonString(ObjectMapper mapper, String json, TypeReference<T> reference) {
        try {
            return mapper.readValue(json, reference);
        } catch (JsonProcessingException e) {
            log.error("json 反序列化失败 clazz: {}, json: {}", reference.getType().getTypeName(), json, e);
            Rethrower.throwAs(e);
            return null;
        }
    }

    /**
     * deserialize json string to target specified by {@link JavaType}.
     */
    public static <T> T parseJsonString(String json, JavaType type) {
        return parseJsonString(OBJECT_MAPPER, json, type);
    }

    /**
     * deserialize json string to target specified by {@link JavaType}.
     * {@link TypeReference} indicate type generics.
     */
    public static <T> T parseJsonString(ObjectMapper mapper, String json, JavaType type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("json 反序列化失败 clazz: {}, json: {}", type.getTypeName(), json, e);
            Rethrower.throwAs(e);
            return null;
        }
    }

    /**
     * deserialize json string to target specified by generic type.
     */
    public static <T> T parseJsonString(String json, Class<T> outerType, Class parameterClasses) {
        return parseJsonString(OBJECT_MAPPER, json, outerType, parameterClasses);
    }

    /**
     * deserialize json string to target specified by generic type.
     */
    public static <T> T parseJsonString(ObjectMapper mapper, String json, Class<T> outerType, Class parameterClasses) {
        JavaType type = mapper.getTypeFactory().constructParametricType(outerType, parameterClasses);
        return parseJsonString(mapper, json, type);
    }

    public static <T> List<T> parseJsonArray(String json, Class<T> clazz) {
        return parseJsonArray(OBJECT_MAPPER, json, clazz);
    }

    public static <T> List<T> parseJsonArray(ObjectMapper mapper, String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }

        try {
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return mapper.readValue(json, listType);
        } catch (Exception e) {
            log.error("json 反序列化为 list 失败 clazz: {}, json: {}", clazz.getName(), json, e);
        }

        return Collections.emptyList();
    }

    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public static JsonNode toJsonNode(Object obj) {
        return OBJECT_MAPPER.valueToTree(obj);
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            Rethrower.throwAs(e);
            return null;
        }
    }

    public static <T> T toObject(JsonNode jsonNode, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(jsonNode, clazz);
    }

    public static <T> T toObject(JsonNode jsonNode, TypeReference<T> typeReference) {
        return OBJECT_MAPPER.convertValue(jsonNode, typeReference);
    }

    public static <T> T toObject(Object object, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(object, clazz);
    }

    public static Map<String, Object> toMap(String json) {
        return parseJsonString(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Map<String, Object> toMap(JsonNode jsonNode) {
        return toObject(jsonNode, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Properties toProps(String json) {
        try {
            return PROPS_MAPPER.writeValueAsProperties(json);
        } catch (IOException e) {
            Rethrower.throwAs(e);
            return null;
        }
    }

    public static boolean checkJsonValid(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }

        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (IOException ignored) {
            // just ignore
        }

        return false;
    }

    public static <T> T merge(T target, T patch, Class<T> resourceClass) {
        JsonNode targetNode = JacksonUtil.toJsonNode(target);
        JsonNode patchNode = JacksonUtil.toJsonNode(patch);
        JsonNode patched = merge(targetNode, patchNode);
        return JacksonUtil.toObject(patched, resourceClass);
    }

    public static JsonNode merge(final JsonNode target, final JsonNode patch) {
        if (!(patch instanceof ObjectNode)) {
            return patch;
        }

        ObjectNode patchObject = (ObjectNode) patch;
        ObjectNode targetObject = target instanceof ObjectNode ? (ObjectNode) target : patchObject.objectNode();

        patch.fields().forEachRemaining(field -> {
            String key = field.getKey();
            JsonNode value = field.getValue();
            if (value.isNull()) {
                targetObject.remove(key);
            } else {
                JsonNode existingValue = targetObject.get(key);
                JsonNode mergeResult = merge(existingValue, value);
                targetObject.replace(key, mergeResult);
            }
        });
        return targetObject;
    }

    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }

}
