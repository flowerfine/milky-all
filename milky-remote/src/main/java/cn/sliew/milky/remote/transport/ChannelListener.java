package cn.sliew.milky.remote.transport;

public interface ChannelListener<T extends TcpChannel> {

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