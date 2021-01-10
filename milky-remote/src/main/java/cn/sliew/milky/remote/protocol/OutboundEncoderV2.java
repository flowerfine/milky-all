package cn.sliew.milky.remote.protocol;

import cn.sliew.milky.remote.TransportMessage;
import cn.sliew.milky.remote.buffer.ChannelBuffer;
import cn.sliew.milky.remote.transport.TcpChannel;

/**
 * fixme 异常处理
 */
public class OutboundEncoderV2 implements OutboundEncoder {

    @Override
    public void encode(TcpChannel channel, TransportMessage message, ChannelBuffer out) {

    }
}
