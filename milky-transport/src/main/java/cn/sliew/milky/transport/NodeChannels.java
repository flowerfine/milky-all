package cn.sliew.milky.transport;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NodeChannels extends AbstractConnection {

    private final List<Channel> channels;

    private final int length;

    private final AtomicInteger counter = new AtomicInteger();

    private final AtomicBoolean closing = new AtomicBoolean(false);

    public NodeChannels(List<Channel> channels) {
        this.channels = channels;
        this.length = channels.size();
    }

    @Override
    public Channel getChannel() {
        return channels.get(Math.floorMod(counter.incrementAndGet(), length));
    }

    @Override
    public void sendRequest(long requestId, TransportRequest request) {
        getChannel().send(request, null);
    }
}
