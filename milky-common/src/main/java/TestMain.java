import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.graph.Graph;

import java.util.concurrent.TimeUnit;

public class TestMain {

    public static void main(String[] args) {
        LoadingCache<String, Graph> graphs = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Graph>() {
                    @Override
                    public Graph load(String s) throws Exception {
                        return null;
                    }
                });
    }
}
