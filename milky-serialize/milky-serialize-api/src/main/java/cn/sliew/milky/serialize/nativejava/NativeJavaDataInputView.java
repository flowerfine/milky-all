package cn.sliew.milky.serialize.nativejava;

import cn.sliew.milky.serialize.DataInputView;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void readFully(@NotNull byte[] b) throws IOException {
        this.inputStream.read(b);
    }

    @Override
    public void readFully(@NotNull byte[] b, int off, int len) throws IOException {
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
        return this.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return this.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return this.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.readLong();
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

    @NotNull
    @Override
    public String readUTF() throws IOException {
        return this.inputStream.readUTF();
    }
}
