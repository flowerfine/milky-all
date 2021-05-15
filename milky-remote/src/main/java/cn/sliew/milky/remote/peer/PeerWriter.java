package cn.sliew.milky.remote.peer;

import java.net.InetSocketAddress;

public interface PeerWriter {

    InetSocketAddress localAddress();

    InetSocketAddress remoteAddress();

    Object settings();

    Object transport();

    Object codec();

    Object protocolHandler();
}
