package cn.sliew.milky.remote.protocol;

import cn.sliew.milky.remote.buffer.ChannelBuffer;
import cn.sliew.milky.remote.transport.TcpChannel;

import java.util.List;

public interface InboundDecoder<T extends TcpChannel> {

    /**
     * Decode bytes into object.
     *
     * @param channel
     * @param in bytes container
     * @param out
     */
    void decode(T channel, ChannelBuffer in, List<Object> out);
}