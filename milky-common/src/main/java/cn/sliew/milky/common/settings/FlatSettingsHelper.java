package cn.sliew.milky.common.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlatSettingsHelper {

    private FlatSettingsHelper() {
        throw new IllegalStateException("no instance");
    }

    /**
     * 将层层嵌套的 {outKey -> {innerkey -> innerValue}} 转换为{outkey.innerKey -> innerValue}.
     * just for debug.
     */
    public static Map<String, Object> flatSettingsMap(Map<String, Object> settings) {
        Map<String, Object> map = new HashMap<>(2);
        settings.forEach((key, value) -> processSetting(map, "", key, value));

        map.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .forEach(entry -> entry.setValue(convertMapsToArrays((Map<String, Object>) entry.getValue())));

        return map;
    }

    public static void processSetting(Map<String, Object> map, String prefix, String setting, Object value) {
        int prefixLength = setting.indexOf('.');
        if (prefixLength == -1) {
            Map<String, Object> innerMap = (Map<String, Object>) map.get(prefix + setting);
            if (innerMap != null) {
                // It supposed to be a value, but we already have a map stored, need to convert this map to "." notation
                innerMap.forEach((innerKey, innerValue) -> map.put(prefix + setting + "." + innerKey, innerValue));
            }
            map.put(prefix + setting, value);
        } else {
            String key = setting.substring(0, prefixLength);
            String rest = setting.substring(prefixLength + 1);
            Object existingValue = map.get(prefix + key);
            if (existingValue == null) {
                Map<String, Object> newMap = new HashMap<>(2);
                processSetting(newMap, "", rest, value);
                map.put(prefix + key, newMap);
            } else {
                if (existingValue instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> innerMap = (Map<String, Object>) existingValue;
                    processSetting(innerMap, "", rest, value);
                    map.put(prefix + key, innerMap);
                } else {
                    // It supposed to be a map, but we already have a value stored, which is not a map
                    // fall back to "." notation
                    processSetting(map, prefix + key + ".", rest, value);
                }
            }
        }
    }

    public static Object convertMapsToArrays(Map<String, Object> map) {
        if (map.isEmpty()) {
            return map;
        }
        boolean isArray = true;
        int maxIndex = -1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (isArray) {
                try {
                    int index = Integer.parseInt(entry.getKey());
                    if (index >= 0) {
                        maxIndex = Math.max(maxIndex, index);
                    } else {
                        isArray = false;
                    }
                } catch (NumberFormatException ex) {
                    isArray = false;
                }
            }
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked") Map<String, Object> valMap = (Map<String, Object>) entry.getValue();
                entry.setValue(convertMapsToArrays(valMap));
            }
        }
        if (isArray && (maxIndex + 1) == map.size()) {
            ArrayList<Object> newValue = new ArrayList<>(maxIndex + 1);
            for (int i = 0; i <= maxIndex; i++) {
                Object obj = map.get(Integer.toString(i));
                if (obj == null) {
                    // Something went wrong. Different format?
                    // Bailout!
                    return map;
                }
                newValue.add(obj);
            }
            return newValue;
        }
        return map;
    }
}
