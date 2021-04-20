package cn.sliew.milky.serialize.nativejava;

import cn.sliew.milky.serialize.DataOutputView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import static cn.sliew.milky.common.check.Requires.require;

public class NativeJavaDataOutputView implements DataOutputView {

    private static final long serialVersionUID = 2668706702125097637L;

    private final ObjectOutputStream outputStream;

    public NativeJavaDataOutputView(OutputStream os) throws IOException {
        this(new ObjectOutputStream(os));
    }

    protected NativeJavaDataOutputView(ObjectOutputStream out) {
        require(out != null, () -> "output is null");
        this.outputStream = out;
    }

    protected ObjectOutputStream getObjectOutputStream() {
        return outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        this.outputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.outputStream.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.outputStream.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.outputStream.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.outputStream.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.outputStream.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.outputStream.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.outputStream.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.outputStream.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.outputStream.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.outputStream.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.outputStream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        this.outputStream.writeUTF(s);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        this.outputStream.writeObject(obj);
    }

    @Override
    public void flushBuffer() throws IOException {
        this.outputStream.flush();
    }
}
