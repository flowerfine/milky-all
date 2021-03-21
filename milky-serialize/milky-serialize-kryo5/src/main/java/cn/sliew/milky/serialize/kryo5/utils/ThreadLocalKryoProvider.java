package cn.sliew.milky.serialize.kryo5.utils;

import cn.sliew.milky.serialize.kryo5.KryoProvider;
import com.esotericsoftware.kryo.kryo5.Kryo;

/**
 * ThreadLocal的潜在的对象无法回收的内存溢出问题。
 */
public class ThreadLocalKryoProvider implements KryoProvider {

    private final ThreadLocal<Kryo> holder = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // Configure the Kryo instance.
        return kryo;
    });


    @Override
    public Kryo get() {
        return holder.get();
    }

}
