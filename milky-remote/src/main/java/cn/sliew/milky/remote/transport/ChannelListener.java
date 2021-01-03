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
     * on exception caught.
     *
     * @param channel   channel.
     * @param exception exception.
     */
    void caught(TcpChannel channel, Throwable exception);
}