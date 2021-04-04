package cn.sliew.milky.serialize.protostuff;

import cn.sliew.milky.serialize.DataInputView;
import cn.sliew.milky.serialize.protostuff.utils.WrapperUtils;
import io.protostuff.GraphIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Protostuff object input implementation
 */
public class ProtostuffDataInputView implements DataInputView {

    private DataInputStream dis;

    public ProtostuffDataInputView(InputStream inputStream) {
        dis = new DataInputStream(inputStream);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.dis.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.dis.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.dis.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.dis.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.dis.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.dis.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return this.dis.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return this.dis.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return this.dis.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.dis.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return this.dis.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return this.dis.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return this.dis.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return this.dis.readUTF();
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Object readObject() throws IOException, ClassNotFoundException {
        int classNameLength = dis.readInt();
        int bytesLength = dis.readInt();

        if (classNameLength < 0 || bytesLength < 0) {
            throw new IOException();
        }

        byte[] classNameBytes = new byte[classNameLength];
        dis.readFully(classNameBytes, 0, classNameLength);

        byte[] bytes = new byte[bytesLength];
        dis.readFully(bytes, 0, bytesLength);

        String className = new String(classNameBytes);
        Class clazz = Class.forName(className);

        Object result;
        if (WrapperUtils.needWrapper(clazz)) {
            Schema<Wrapper> schema = RuntimeSchema.getSchema(Wrapper.class);
            Wrapper wrapper = schema.newMessage();
            GraphIOUtil.mergeFrom(bytes, wrapper, schema);
            result = wrapper.getData();
        } else {
            Schema schema = RuntimeSchema.getSchema(clazz);
            result = schema.newMessage();
            GraphIOUtil.mergeFrom(bytes, result, schema);
        }

        return result;
    }

}
