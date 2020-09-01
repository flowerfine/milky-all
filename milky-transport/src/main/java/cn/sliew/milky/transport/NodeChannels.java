package cn.sliew.milky.transport;

import cn.sliew.milky.transport.exchange.TransportRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NodeChannels extends AbstractConnection {

    private final List<TcpChannel> tcpChannels;

    private final TcpChannelSelectStrategy channelSelectStrategy;

    private final AtomicBoolean closing = new AtomicBoolean(false);

    public NodeChannels(List<TcpChannel> tcpChannels, TcpChannelSelectStrategy channelSelectStrategy) {
        this.tcpChannels = tcpChannels;
        this.channelSelectStrategy = channelSelectStrategy;
    }

    @Override
    public TcpChannelSelectStrategy selectStrategy() {
        return channelSelectStrategy;
    }

    @Override
    public TcpChannel channel() {
        return channelSelectStrategy.select(this.tcpChannels);
    }

    @Override
    public void sendRequest(long requestId, TransportRequest request) {
        channel().sendMessage(request, null);
    }
}
