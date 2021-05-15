package cn.sliew.milky.remote.transport;

/**
 * todo 分别对应着 netty最主要的几个事件
 *
 * fixme 应该称作 ChannelHandler，而不是 listener。
 * @param <T>
 */
public interface ChannelHandler<T extends TcpChannel> {

    /**
     * on channel connected.
     *
     * @param channel channel.
     */
    void connected(TcpChannel channel);

    /**
     * on channel disconnected.
     *
     * @param channel channel.
     */
    void disconnected(TcpChannel channel);

    /**
     * on message sent.
     *
     * @param channel channel.
     * @param message message.
     */
    void sent(TcpChannel channel, Object message);

    /**
     * on message received.
     *
     * @param channel channel.
     * @param message message.
     */
    void received(TcpChannel channel, Object message);

    /**
     * on exception caught.
     *
     * @param channel   channel.
     * @param exception exception.
     */
    void caught(TcpChannel channel, Throwable exception);
}