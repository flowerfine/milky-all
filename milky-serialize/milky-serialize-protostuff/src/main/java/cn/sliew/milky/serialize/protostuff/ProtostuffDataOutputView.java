
package cn.sliew.milky.serialize.protostuff;

import cn.sliew.milky.serialize.DataOutputView;
import cn.sliew.milky.serialize.protostuff.utils.WrapperUtils;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Protostuff object output implementation
 */
public class ProtostuffDataOutputView implements DataOutputView {

    private LinkedBuffer buffer = LinkedBuffer.allocate();
    private DataOutputStream dos;

    public ProtostuffDataOutputView(OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);
    }

    @Override
    public void write(int b) throws IOException {
        this.dos.write(b);
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        this.dos.write(b);
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        this.dos.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.dos.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.dos.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.dos.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.dos.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.dos.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.dos.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.dos.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.dos.writeDouble(v);
    }

    @Override
    public void writeBytes(@NotNull String s) throws IOException {
        this.dos.writeBytes(s);
    }

    @Override
    public void writeChars(@NotNull String s) throws IOException {
        this.dos.writeChars(s);
    }

    @Override
    public void writeUTF(@NotNull String s) throws IOException {
        this.dos.writeUTF(s);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeObject(Object obj) throws IOException {
        byte[] bytes;
        byte[] classNameBytes;

        try {
            if (obj == null || WrapperUtils.needWrapper(obj)) {
                Schema<Wrapper> schema = RuntimeSchema.getSchema(Wrapper.class);
                Wrapper wrapper = new Wrapper(obj);
                bytes = GraphIOUtil.toByteArray(wrapper, schema, buffer);
                classNameBytes = Wrapper.class.getName().getBytes();
            } else {
                Schema schema = RuntimeSchema.getSchema(obj.getClass());
                bytes = GraphIOUtil.toByteArray(obj, schema, buffer);
                classNameBytes = obj.getClass().getName().getBytes();
            }
        } finally {
            buffer.clear();
        }

        dos.writeInt(classNameBytes.length);
        dos.writeInt(bytes.length);
        dos.write(classNameBytes);
        dos.write(bytes);
    }

    @Override
    public void flushBuffer() throws IOException {
        this.dos.flush();
    }
}
