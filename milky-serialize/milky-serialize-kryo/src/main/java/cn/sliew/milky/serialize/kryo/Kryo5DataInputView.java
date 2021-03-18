package cn.sliew.milky.serialize.kryo;

import cn.sliew.milky.serialize.DataInputView;
import cn.sliew.milky.serialize.kryo.utils.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class Kryo5DataInputView implements DataInputView {

    private final Kryo kryo;
    private final Input input;

    public Kryo5DataInputView(InputStream inputStream) {
        this.kryo = KryoUtils.get();
        this.input = new Input(inputStream);
    }

    @Override
    public void readFully(@NotNull byte[] b) throws IOException {
        try {
            input.read(b);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void readFully(@NotNull byte[] b, int off, int len) throws IOException {
        try {
            input.read(b, off, len);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int skipBytes(int n) throws IOException {
        try {
            return (int) input.skip(Integer.valueOf(n).longValue());
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        try {
            return input.readBoolean();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public byte readByte() throws IOException {
        try {
            return input.readByte();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        try {
            return input.readByteUnsigned();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public short readShort() throws IOException {
        try {
            return input.readShort();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int readUnsignedShort() throws IOException {
        try {
            return input.readShortUnsigned();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public char readChar() throws IOException {
        try {
            return input.readChar();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            return input.readInt();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            return input.readLong();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public float readFloat() throws IOException {
        try {
            return input.readFloat();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public double readDouble() throws IOException {
        try {
            return input.readDouble();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String readLine() throws IOException {
        try {
            return input.readString();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @NotNull
    @Override
    public String readUTF() throws IOException {
        try {
            return input.readString();
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        try {
            // TODO carries class info every time.
            return kryo.readClassAndObject(input);
        } catch (KryoException e) {
            throw new IOException(e);
        }
    }

}
