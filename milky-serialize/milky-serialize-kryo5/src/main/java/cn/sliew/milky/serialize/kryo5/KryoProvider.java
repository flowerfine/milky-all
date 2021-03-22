package cn.sliew.milky.serialize.kryo5;

import com.esotericsoftware.kryo.kryo5.Kryo;

@FunctionalInterface
public interface KryoProvider {

    Kryo get();

    default void release(Kryo kryo) {

    }
}
