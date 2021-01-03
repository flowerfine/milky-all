package cn.sliew.milky.remote;

import java.net.InetSocketAddress;

public abstract class TransportMessage {

    /**
     * fixme 远端的address，使用InetSocketAddress可能不太好
     */
    private InetSocketAddress remoteAddress;
}
