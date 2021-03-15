package cn.sliew.milky.serialize;

import java.io.DataInput;
import java.io.IOException;
import java.io.Serializable;

public interface DataInputView extends DataInput, Serializable {

    Object readObject() throws IOException, ClassNotFoundException;

    default byte[] readBytes() throws IOException {
        int length = this.readInt();
        if (length < 0) {
            return null;
        }
        if (length == 0) {
            return new byte[0];
        }

        byte[] bytes = new byte[length];
        this.readFully(bytes, 0, length);
        return bytes;
    }

}
