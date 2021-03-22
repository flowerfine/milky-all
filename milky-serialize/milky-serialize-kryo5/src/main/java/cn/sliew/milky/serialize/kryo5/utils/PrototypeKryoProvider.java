package cn.sliew.milky.serialize.kryo5.utils;

import cn.sliew.milky.serialize.kryo5.KryoProvider;
import com.esotericsoftware.kryo.kryo5.Kryo;

public class PrototypeKryoProvider implements KryoProvider {

    @Override
    public Kryo get() {
        return new Kryo();
    }
}
