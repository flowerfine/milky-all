package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.primitives.Strings;
import cn.sliew.milky.common.regex.Regex;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static cn.sliew.milky.common.check.Ensures.checkArgument;

/**
 * {@link Setting}s with prefix.
 * jackson 的 {@link ObjectNode} 可以替代 GroupKey
 */
public final class GroupKey extends SimpleKey {

    public GroupKey(String key) {
        this(key, Strings.empty());
    }

    public GroupKey(String key, String description) {
        super(key, description);
        checkArgument(key.endsWith("."), () -> "key must end with a '.'");
    }

    @Override
    public boolean match(String toTest) {
        return Regex.simpleMatch(key + "*", toTest);
    }
    
}