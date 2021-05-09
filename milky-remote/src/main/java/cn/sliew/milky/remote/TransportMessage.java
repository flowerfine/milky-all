package cn.sliew.milky.remote;

import java.net.InetSocketAddress;

/**
 * todo 添加优先级信息，用于在发送的时候进行优先发送
 */
public abstract class TransportMessage {

    /**
     * fixme 远端的address，使用InetSocketAddress可能不太好
     */
    private InetSocketAddress remoteAddress;
}
