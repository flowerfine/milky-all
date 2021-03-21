package cn.sliew.milky.cache.support;

import java.io.Serializable;

public final class NullValue implements Serializable {

    private static final long serialVersionUID = 2334728105084753521L;

    public static final Object INSTANCE = new NullValue();
}
