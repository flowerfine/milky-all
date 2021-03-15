package cn.sliew.milky.serialize.java;

import cn.sliew.milky.serialize.nativejava.NativeJavaDataInputView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Java object input implementation
 */
public class JavaDataInputView extends NativeJavaDataInputView {

    public JavaDataInputView(InputStream is) throws IOException {
        super(new ObjectInputStream(is));
    }

    public JavaDataInputView(InputStream is, boolean compacted) throws IOException {
        super(compacted ? new CompactedObjectInputStream(is) : new ObjectInputStream(is));
    }

    @Override
    public String readUTF() throws IOException {
        int len = getObjectInputStream().readInt();
        if (len < 0) {
            return null;
        }

        return getObjectInputStream().readUTF();
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        byte b = getObjectInputStream().readByte();
        if (b == 0) {
            return null;
        }

        return getObjectInputStream().readObject();
    }

}
