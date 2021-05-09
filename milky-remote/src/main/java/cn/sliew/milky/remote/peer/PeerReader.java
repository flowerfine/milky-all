package cn.sliew.milky.remote.peer;

import java.net.InetSocketAddress;

public interface PeerReader {

    InetSocketAddress localAddress();

    InetSocketAddress remoteAddress();

    Object settings();

    Object transport();

    Object codec();

    Object dispatcher();


}
