package cn.sliew.milky.property;

import cn.sliew.milky.test.MilkyTestCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsTestCase extends MilkyTestCase {

    private JsonNode source;

    @BeforeEach
    private void beforeEach() throws URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        Path resource = Paths.get(url.toURI());
        Path json = resource.resolve("users.yml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        this.source = mapper.readTree(json.toFile());
    }
}
