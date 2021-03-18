package cn.sliew.milky.serialize.kryo;

import cn.sliew.milky.serialize.DataOutputView;
import cn.sliew.milky.serialize.kryo.utils.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class Kryo5DataOutputView implements DataOutputView {

    private final Kryo kryo;
    private final Output output;

    public Kryo5DataOutputView(OutputStream outputStream) {
        this.kryo = KryoUtils.get();
        this.output = new Output(outputStream);
    }

    @Override
    public void write(int b) throws IOException {
        this.output.write(b);
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        output.write(b);
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        output.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        output.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        output.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        output.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        output.writeChar((char) v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        output.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        output.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        output.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        output.writeDouble(v);
    }

    @Override
    public void writeBytes(@NotNull String s) throws IOException {
        output.writeString(s);
    }

    @Override
    public void writeChars(@NotNull String s) throws IOException {
        output.writeString(s);
    }

    @Override
    public void writeUTF(@NotNull String s) throws IOException {
        output.writeString(s);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        // TODO carries class info every time.
        kryo.writeClassAndObject(output, obj);
    }

    @Override
    public void flushBuffer() throws IOException {
        output.flush();
    }

}
