package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.regex.Regex;

import static cn.sliew.milky.common.check.Ensures.checkArgument;

public final class GroupKey extends SimpleKey {

    public GroupKey(int id, String key) {
        super(id, key);
        checkArgument(key.endsWith(".") == false, () -> "key must end with a '.'");
    }

    @Override
    public boolean match(String toTest) {
        return Regex.simpleMatch(name() + "*", toTest);
    }
}