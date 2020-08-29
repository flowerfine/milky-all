package cn.sliew.milky.transport;

public interface ChannelListener<T extends Channel> {

    /**
     * on channel connected.
     *
     * @param channel channel.
     */
    void connected(T channel);

    /**
     * on channel disconnected.
     *
     * @param channel channel.
     */
    void disconnected(T channel);

    /**
     * on message sent.
     *
     * @param channel channel.
     * @param message message.
     */
    void sent(T channel, Object message);

    /**
     * on message received.
     *
     * @param channel channel.
     * @param message message.
     */
    void received(T channel, Object message);

    /**
     * on exception caught.
     *
     * @param channel   channel.
     * @param exception exception.
     */
    void caught(T channel, Throwable exception);
}
