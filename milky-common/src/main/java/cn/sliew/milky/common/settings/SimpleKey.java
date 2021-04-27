package cn.sliew.milky.common.settings;

public class SimpleKey extends Key {

    public SimpleKey(String key) {
        super(key);
    }

    @Override
    boolean match(String key) {
        return name().equals(key);
    }

}
