package cn.sliew.milky.common.settings;

import cn.sliew.milky.common.constant.AbstractConstant;

public abstract class Key extends AbstractConstant {

    /**
     * Creates a new instance.
     *
     * @param id
     * @param name
     */
    protected Key(int id, String name) {
        super(id, name);
    }

    abstract boolean match(String key);
}