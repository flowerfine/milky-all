package cn.sliew.milky.remote.transport;

import java.io.Serializable;

public interface OutboundEncoder<T extends TcpChannel> {

    /**
     * Encode object into bytes.
     *
     * @param channel
     * @param msg
     * @param out bytes container
     */
    void encode(T channel, Serializable msg, Object out);
}