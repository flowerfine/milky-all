package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.constant.AbstractConstant;

public abstract class Key extends AbstractConstant {

    /**
     * Creates a new instance.
     *
     * @param key key
     */
    protected Key(String key) {
        super(0, key);
    }

    abstract boolean match(String key);

    @Override
    public final String toString() {
        return String.format("Key{name=%s}", name());
    }

}