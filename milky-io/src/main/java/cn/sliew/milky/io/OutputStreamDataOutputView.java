package cn.sliew.milky.io;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamDataOutputView implements DataOutputView {

    private final OutputStream out;

    public OutputStreamDataOutputView(OutputStream out) {
        if (out == null) {
            throw new IllegalArgumentException("'out' null");
        }
        this.out = out;
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    private static final ThreadLocal<byte[]> scratch = ThreadLocal.withInitial(() -> new byte[1024]);

    /**
     * fixme buffer recycler
     */
    @Override
    public byte[] getBuffer() {
        return scratch.get();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void writeByte(int v) throws IOException {
        out.write(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        
    }

    @Override
    public void writeChars(String s) throws IOException {

    }

    @Override
    public void writeUTF(String s) throws IOException {

    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
