package cn.sliew.milky.transport;

public interface ChannelHandler<T extends TcpChannel> {

    /**
     * on channel connected. outbound event
     *
     * @param channel channel.
     */
    void connected(T channel);

    /**
     * on channel disconnected. outbound event
     *
     * @param channel channel.
     */
    void disconnected(T channel);

    /**
     * on message sent. inbound event
     *
     * @param channel channel.
     * @param message message.
     */
    void send(T channel, Object message);

    /**
     * on message received. inbound event
     *
     * @param channel channel.
     * @param message message.
     */
    void receive(T channel, Object message);

    /**
     * on exception caught.
     *
     * @param channel   channel.
     * @param exception exception.
     */
    void caught(T channel, Throwable exception);
}
