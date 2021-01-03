package cn.sliew.milky.transport.backup;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.transport.ActionListener;
import cn.sliew.milky.transport.Node;
import cn.sliew.milky.transport.TcpChannel;

public class ChannelConnectListener implements ActionListener<Void> {

    private static final Logger logger = LoggerFactory.getLogger(ChannelConnectListener.class);

    private final Node node;
    private final TcpChannel channel;
    private final ActionListener<TcpChannel> listener;

    ChannelConnectListener(Node node, TcpChannel channel, ActionListener<TcpChannel> listener) {
        this.node = node;
        this.channel = channel;
        this.listener = listener;
    }

    @Override
    public void onResponse(Void unused) {
        logger.info("执行握手");
        logger.info("执行心跳");
        listener.onResponse(channel);
    }

    @Override
    public void onFailure(Exception e) {
        channel.close();
        listener.onFailure(e);
    }

    public void onTimeout() {
        channel.close();
        listener.onFailure(new RuntimeException("connect timeout"));
    }
}