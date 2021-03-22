package cn.sliew.milky.serialize.kryo5.utils;

import cn.sliew.milky.serialize.kryo5.KryoProvider;
import com.esotericsoftware.kryo.kryo5.Kryo;

public class Kryo5Utils {

    private static KryoProvider provider = new PoolKryoProvider();

    private Kryo5Utils() {
        throw new IllegalStateException("can't do this");
    }

    public static Kryo get() {
        return provider.get();
    }

    public static void release(Kryo kryo) {
        provider.release(kryo);
    }
    
}
