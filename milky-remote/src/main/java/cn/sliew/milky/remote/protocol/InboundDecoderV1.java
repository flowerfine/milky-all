package cn.sliew.milky.remote.protocol;

import cn.sliew.milky.common.io.stream.StreamInput;
import cn.sliew.milky.remote.buffer.ChannelBuffer;
import cn.sliew.milky.remote.transport.TcpChannel;

import java.util.List;

public class InboundDecoderV1 implements InboundDecoder {

    private int lessLen;

    {
        lessLen = RpcProtocolV2.getResponseHeaderLength() < RpcProtocolV2.getRequestHeaderLength() ? RpcProtocolV2
                .getResponseHeaderLength() : RpcProtocolV2.getRequestHeaderLength();
    }



    @Override
    public void decode(TcpChannel channel, StreamInput in, List out) {

    }
}
