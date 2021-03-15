package cn.sliew.milky.serialize;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public interface DataOutputView extends DataOutput, Serializable {

    void writeObject(Object obj) throws IOException;

    void flushBuffer() throws IOException;

}
