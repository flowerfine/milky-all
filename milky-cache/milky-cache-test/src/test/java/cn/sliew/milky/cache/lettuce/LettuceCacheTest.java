package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.base.AbstractCacheTest;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

public class LettuceCacheTest extends AbstractCacheTest {

    {
        LettuceCacheOptions options = new LettuceCacheOptions();
        options.name("LettuceCacheTest");
        Escaper escaper = UrlEscapers.urlPathSegmentEscaper();
        String escape = escaper.escape("123");
        System.out.println(escape);
        String uriStr = "redis://" + escape + "@localhost:6379/0?timeout=1s";
        System.out.println(uriStr);
        options.redisURI(uriStr);
        LettuceCacheFactory factory = new LettuceCacheFactory();
        this.cache = factory.getCache(options);
    }

}
