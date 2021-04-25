package cn.sliew.milky.common.environment;

import java.util.Map;
import java.util.Properties;

public class PropertiesPropertySource extends MapPropertySource {

    public PropertiesPropertySource(String name, Properties source) {
        super(name, (Map)source);
    }

    protected PropertiesPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }
}
