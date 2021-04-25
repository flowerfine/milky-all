package cn.sliew.milky.common.environment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapPropertySource extends AbstractPropertySource<Map<String, Object>>  {

    public MapPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override
    public boolean containsProperty(String property) {
        return this.source.containsKey(property);
    }

    @Override
    public Optional<Object> getProperty(String property) {
        return Optional.ofNullable(this.source.get(property));
    }

    @Override
    public List<String> getAllPropertyNames() {
        return this.source.keySet().stream().collect(Collectors.toList());
    }
}
