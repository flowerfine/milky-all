package cn.sliew.milky.serialize;

import java.io.IOException;

public interface Readable {

    void readFrom(DataInputView in) throws IOException;

    @FunctionalInterface
    interface Reader<V> {

        V read(DataInputView in) throws IOException;
    }

}
