package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.ActionListener;
import cn.sliew.milky.remote.transport.Node;

import java.time.Duration;

public class DefaultExchanger implements Exchanger {

    private final OutboundHandler outboundHandler;
    private final InboundHandler inboundHandler;

    public DefaultExchanger() {
        this.outboundHandler = null;
        this.inboundHandler = null;
    }

    @Override
    public void openConnection(Node node, ConnectionProfile profile, ActionListener<Connection> listener) {

    }

    @Override
    public void setMessageListener(MessageListener listener) {
        outboundHandler.setMessageListener(listener);
        inboundHandler.setMessageListener(listener);
    }

    public void setSlowLogThreshold(Duration slowLogThreshold) {
        inboundHandler.setSlowLogThresholdMs(slowLogThreshold.toMillis());
    }
}
