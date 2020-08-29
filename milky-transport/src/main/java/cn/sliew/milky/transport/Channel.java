package cn.sliew.milky.transport;

import java.net.InetSocketAddress;

public interface Channel {

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
     * is closed.
     *
     * @return closed
     */
    boolean isClosed();

    /**
     * close the channel.
     */
    void close();

    /**
     * Graceful close the channel.
     */
    void close(int timeout);


}
