package cn.sliew.milky.remote.protocol;

import cn.sliew.milky.remote.TransportMessage;
import cn.sliew.milky.remote.buffer.ChannelBuffer;
import cn.sliew.milky.remote.transport.TcpChannel;

public interface OutboundEncoder<T extends TcpChannel> {

    /**
     * Encode object into bytes.
     *
     * @param channel
     * @param message
     * @param out     bytes container
     */
    void encode(T channel, TransportMessage message, ChannelBuffer out);
}