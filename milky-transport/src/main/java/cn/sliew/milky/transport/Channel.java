package cn.sliew.milky.transport;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * channel，负责底层的发送消息功能
 */
public interface Channel extends Closeable {

    /**
     * get local address.
     *
     * @return local address.
     */
    InetSocketAddress getLocalAddress();

    /**
     * get remote address.
     *
     * @return remote address.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Sends a tcp message to the channel. The listener will be executed once the send process has been
     * completed.
     *
     * @param message  to send to channel
     * @param listener to execute upon send completion
     */
    void send(Object message, ActionListener<Void> listener);

    /**
     * Indicates whether a channel is currently open
     *
     * @return boolean indicating if channel is open
     */
    boolean isOpen();

    /**
     * is closed.
     *
     * @return closed
     */
    boolean isClosed();

    /**
     * close the channel.
     */
    @Override
    void close();

    /**
     * Graceful close the channel.
     */
    void close(int timeout);


}
