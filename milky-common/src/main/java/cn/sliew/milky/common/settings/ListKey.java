package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.primitives.Strings;

import java.util.regex.Pattern;

public final class ListKey extends SimpleKey {
    private final Pattern pattern;

    public ListKey(String key) {
        this(key, Strings.empty());
    }

    public ListKey(String key, String description) {
        super(key, description);
        this.pattern = Pattern.compile(Pattern.quote(key) + "(\\.\\d+)?");
    }

    @Override
    public boolean match(String toTest) {
        return pattern.matcher(toTest).matches();
    }

}