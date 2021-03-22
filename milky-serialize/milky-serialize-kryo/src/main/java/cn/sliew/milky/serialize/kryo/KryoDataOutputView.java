package cn.sliew.milky.serialize.kryo;

import cn.sliew.milky.serialize.DataOutputView;
import cn.sliew.milky.serialize.kryo.utils.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class KryoDataOutputView implements DataOutputView {

    private final Kryo kryo;
    private final Output output;

    public KryoDataOutputView(OutputStream outputStream) {
        this.kryo = KryoUtils.get();
        this.output = new Output(outputStream);
    }

    @Override
    public void write(int b) throws IOException {
        try {
            this.output.write(b);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        try {
            this.output.write(b);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) throws IOException {
        try {
            output.write(b, off, len);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        try {
            output.writeBoolean(v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeByte(int v) throws IOException {
        try {
            output.writeByte(v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeShort(int v) throws IOException {
        try {
            output.writeShort(v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        try {
            output.writeChar((char) v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        try {
            output.writeInt(v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        try {
            output.writeLong(v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        try {
            output.writeFloat(v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        try {
            output.writeDouble(v);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeBytes(@NotNull String s) throws IOException {
        try {
            output.writeString(s);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeChars(@NotNull String s) throws IOException {
        try {
            output.writeString(s);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeUTF(@NotNull String s) throws IOException {
        try {
            output.writeString(s);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        try {
            // TODO carries class info every time.
            kryo.writeClassAndObject(output, obj);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        try {
            output.flush();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

}
