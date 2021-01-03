package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.TcpChannel;

import java.util.List;

/**
 * Select strategy from channel pool
 */
public interface TcpChannelSelectStrategy {
    /**
     * select strategy
     *
     * @param channels source channels
     * @return selected channel
     */
    TcpChannel select(List<TcpChannel> channels);
}