package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.regex.Regex;

import static cn.sliew.milky.common.check.Ensures.checkArgument;

public final class GroupKey extends SimpleKey {

    public GroupKey(String key, String description) {
        super(key, description);
        checkArgument(key.endsWith(".") == false, () -> "key must end with a '.'");
        if (key.endsWith(".") == false) {
            throw new IllegalArgumentException("key must end with a '.'");
        }
    }

    @Override
    public boolean match(String toTest) {
        return Regex.simpleMatch(key + "*", toTest);
    }
    
}