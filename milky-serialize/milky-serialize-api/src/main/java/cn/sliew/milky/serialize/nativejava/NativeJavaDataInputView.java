package cn.sliew.milky.serialize.nativejava;

import cn.sliew.milky.serialize.DataInputView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import static cn.sliew.milky.common.check.Requires.require;

public class NativeJavaDataInputView implements DataInputView {

    private static final long serialVersionUID = -3443718316139099062L;

    private final ObjectInputStream inputStream;

    public NativeJavaDataInputView(InputStream is) throws IOException {
        this(new ObjectInputStream(is));
    }

    protected NativeJavaDataInputView(ObjectInputStream is) {
        require(is != null, "input is null");
        this.inputStream = is;
    }

    protected ObjectInputStream getObjectInputStream() {
        return inputStream;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.inputStream.read(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.inputStream.read(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.inputStream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.inputStream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.inputStream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.inputStream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return this.inputStream.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.inputStream.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return this.inputStream.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return this.inputStream.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.inputStream.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return this.inputStream.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return this.inputStream.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return this.inputStream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return this.inputStream.readUTF();
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        return this.inputStream.readObject();
    }

}
