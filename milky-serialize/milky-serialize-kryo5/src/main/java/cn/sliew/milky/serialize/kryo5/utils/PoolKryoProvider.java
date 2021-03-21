package cn.sliew.milky.serialize.kryo5.utils;

import cn.sliew.milky.serialize.kryo5.KryoProvider;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.util.Pool;

public class PoolKryoProvider implements KryoProvider {

    private  final Pool<Kryo> kryoPool = new Pool<Kryo>(true, true, 8) {
        protected Kryo create () {
            Kryo kryo = new Kryo();
            // Configure the Kryo instance.
            return kryo;
        }
    };

    @Override
    public Kryo get() {
        return kryoPool.obtain();
    }

    @Override
    public void release(Kryo kryo) {
        kryoPool.free(kryo);
    }
}
