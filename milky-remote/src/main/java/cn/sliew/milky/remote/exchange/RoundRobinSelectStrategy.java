package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.TcpChannel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinSelectStrategy implements TcpChannelSelectStrategy {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public TcpChannel select(List<TcpChannel> channels) {
        if (channels.size() == 0) {
            throw new IllegalStateException("can't select channel size is 0");
        }
        return channels.get(Math.floorMod(counter.incrementAndGet(), channels.size()));
    }
}