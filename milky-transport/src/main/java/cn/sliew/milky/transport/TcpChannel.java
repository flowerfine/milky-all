package cn.sliew.milky.transport;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * channel，负责底层的发送消息功能
 * 扩展类
 */
public interface TcpChannel extends Closeable {

    boolean isServer();

    /**
     * eg. netty4
     *
     * @return channel type
     */
    String getChannelType();

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
    void sendMessage(Object message, ActionListener<Void> listener);

    void sendResponse(TransportResponse response) throws IOException;

    void sendResponse(Exception exception) throws IOException;

    /**
     * Indicates whether a channel is currently open
     *
     * @return boolean indicating if channel is open
     */
    boolean isOpen();

    /**
     * close the channel.
     */
    @Override
    void close();
}
