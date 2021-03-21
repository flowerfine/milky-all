package cn.sliew.milky.cache.caffeine;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CaffeineCacheTest extends MilkyTestCase {

    private CaffeineCache cache;

    @BeforeEach
    public void beforeEach() {
        CaffeineCacheOptions options = new CaffeineCacheOptions();
        options.name("caffeine-cache");
        CaffeineCacheFactory factory = new CaffeineCacheFactory();
        this.cache = (CaffeineCache) factory.getCache(options);
    }

    @Test
    public void testGet() {
        this.cache.put("123", "456");
        assertEquals("456", this.cache.get("123"));
    }
}
