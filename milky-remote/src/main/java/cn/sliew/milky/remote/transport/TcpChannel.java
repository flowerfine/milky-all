package cn.sliew.milky.remote.transport;

import java.net.InetSocketAddress;

/**
 * 表示一个tcp层面的channel，也代表一个客户端，能够执行通信
 * 因为服务端的channel在接受连接后与客户端进行通信的也是通过TcpChannel,
 * 所以增加方法判断是否为服务端的channel
 * <p>
 * 同时需要由TcpChannel子类实现listener的监听器方法. 监听连接和断连事件
 * <p>
 * 交给netty进行实现了
 * <p>
 * Component which is capable of I/O operations such as read, write, connect, and bind.
 */
public interface TcpChannel extends Channel {

    /**
     * Indicates if the channel is an inbound server channel.
     */
    boolean isServerChannel();

    /**
     * Returns the IO operation such as connect、disconnect、read or write handler for this channel.
     *
     * @return the channel handler.
     */
    ChannelHandler getChannelHandler();

    /**
     * Returns the local address for this channel.
     *
     * @return the local address of this channel.
     */
    InetSocketAddress getLocalAddress();

    /**
     * Returns the remote address for this channel. Can be null if channel does not have a remote address.
     *
     * @return the remote address of this channel.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Sends a tcp message to the channel. The listener will be executed once the send process has been
     * completed.
     *
     * @param message to send to channel
     */
    void sendMessage(byte[] message);
}