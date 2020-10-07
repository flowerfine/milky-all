package cn.sliew.milky.transport.netty;

import cn.sliew.milky.transport.TcpChannel;
import cn.sliew.milky.transport.TcpChannelSelectStrategy;

import java.util.List;
import java.util.Random;

public class RandomTcpChannelSelectStrategy implements TcpChannelSelectStrategy {

    private static final Random random = new Random();

    @Override
    public TcpChannel select(List<TcpChannel> channels) {
        return channels.get(random.nextInt(channels.size() - 1));
    }
}
