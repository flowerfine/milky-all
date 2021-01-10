package cn.sliew.milky.remote.protocol;

import cn.sliew.milky.common.io.stream.StreamOutput;
import cn.sliew.milky.remote.transport.TcpChannel;

public interface OutboundEncoder<T extends TcpChannel> {

    /**
     * Encode object into bytes.
     *
     * @param channel
     * @param message
     * @param out     bytes container
     */
    void encode(T channel, Object message, StreamOutput out);
}