package cn.sliew.milky.remote.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class TcpTransport implements Transport {

    // this lock is here to make sure we close this transport and disconnect all the client nodes
    // connections while no connect operations is going on
    private final ReadWriteLock closeLock = new ReentrantReadWriteLock();

    @Override
    public TcpChannel connnect(Node node, ChannelListener listener) {
        try {
            TcpChannel channel = connect(node, listener);
            channel.registerListener(listener);
            //todo timeout
            // 处理连接超时问题
            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TcpServerChannel bind(InetSocketAddress address, ChannelListener listener) {
        closeLock.writeLock().lock();
        try {
            TcpServerChannel channel = doBind(address, listener);
            //todo timeout
            // 处理bind超时问题
            return channel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeLock.writeLock().unlock();
        }
    }

    /**
     * Initiate a single tcp socket channel.
     *
     * @param node for the initiated connection
     * @return the pending connection
     * @throws IOException if an I/O exception occurs while opening the channel
     */
    protected abstract TcpChannel connect(Node node, ChannelListener listener) throws IOException;

    protected abstract TcpServerChannel doBind(InetSocketAddress address, ChannelListener listener) throws IOException;
}
