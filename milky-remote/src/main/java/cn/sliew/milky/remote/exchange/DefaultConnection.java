package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.concurrent.CompletableContext;
import cn.sliew.milky.remote.TransportRequest;
import cn.sliew.milky.remote.transport.Node;
import cn.sliew.milky.remote.transport.TcpChannel;

import java.io.IOException;
import java.util.List;

public class DefaultConnection implements Connection {

    private volatile TcpChannelSelectStrategy selectStrategy;

    private final CompletableContext<Void> closeContext = new CompletableContext<>();

    private final Node node;
    private final List<TcpChannel> channels;

    private final OutboundHandler outboundHandler;

    public DefaultConnection(Node node, List<TcpChannel> channels, OutboundHandler outboundHandler) {
        this.node = node;
        this.channels = channels;
        this.outboundHandler = outboundHandler;
        this.selectStrategy = new RoundRobinSelectStrategy();
    }

    @Override
    public TcpChannelSelectStrategy selectStrategy() {
        return this.selectStrategy;
    }

    @Override
    public TcpChannel channel() {
        return selectStrategy().select(channels);
    }

    @Override
    public void sendRequest(long requestId, String action, TransportRequest request) {
        try {
            outboundHandler.sendRequest(node, channel(), requestId, action, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isClosed() {
        return closeContext.isDone();
    }

    @Override
    public void close() {
        closeContext.complete(null);
    }
}
