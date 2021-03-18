package cn.sliew.milky.serialize.kryo.utils;

import com.esotericsoftware.kryo.Kryo;

public class PrototypeKryoFactory extends AbstractKryoFactory {

    @Override
    public void returnKryo(Kryo kryo) {
        // do nothing
    }

    @Override
    public Kryo getKryo() {
        return create();
    }
}
