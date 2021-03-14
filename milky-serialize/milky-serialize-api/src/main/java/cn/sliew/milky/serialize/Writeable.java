package cn.sliew.milky.serialize;

import java.io.IOException;

public interface Writeable {

    void writeTo(DataOutputView out) throws IOException;

    @FunctionalInterface
    interface Writer<V> {

        void write(DataOutputView out, V value) throws IOException;
    }

}
