package cn.sliew.milky.common.settings;

import java.util.regex.Pattern;

public final class ListKey extends SimpleKey {
    private final Pattern pattern;

    public ListKey(String key) {
        super(key);
        this.pattern = Pattern.compile(Pattern.quote(key) + "(\\.\\d+)?");
    }

    @Override
    public boolean match(String toTest) {
        return pattern.matcher(toTest).matches();
    }

}