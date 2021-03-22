package cn.sliew.milky.cache.support;

import java.io.Serializable;

public class NullKey implements Serializable {

    private static final long serialVersionUID = 5668630189423384615L;

    public static final Object INSTANCE = new NullKey();
}
