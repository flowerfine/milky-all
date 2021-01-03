package cn.sliew.milky.remote.transport;

public abstract class AbstractChannel implements Channel {

    private volatile boolean closed;

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
