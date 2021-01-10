package cn.sliew.milky.common.io.stream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamStreamInput extends StreamInput {

    private final InputStream is;

    /**
     * Creates a new InputStreamStreamInput with unlimited size
     *
     * @param is the input stream to wrap
     */
    public InputStreamStreamInput(InputStream is) {
        this.is = is;
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public byte readByte() throws IOException {
        int ch = is.read();
        if (ch < 0)
            throw new EOFException();
        return (byte) (ch);
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        final int read = readFully(is, b, offset, len);
        if (read != len) {
            throw new EOFException();
        }
    }

    private int readFully(InputStream reader, byte[] dest, int offset, int len) throws IOException {
        int read = 0;
        while (read < len) {
            final int r = reader.read(dest, offset + read, len - read);
            if (r == -1) {
                break;
            }
            read += r;
        }
        return read;
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }
}