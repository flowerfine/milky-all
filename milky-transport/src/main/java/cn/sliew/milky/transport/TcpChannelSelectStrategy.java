package cn.sliew.milky.transport;

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