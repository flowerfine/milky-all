package cn.sliew.milky.remote.netty4.example;

import cn.sliew.milky.remote.netty4.Netty4Transport;
import cn.sliew.milky.remote.transport.Node;
import cn.sliew.milky.remote.transport.TcpChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.UUID;

public class Netty4TransportMain {

    public static void main(String[] args) throws Exception {
        Netty4Transport transport = new Netty4Transport();
        transport.bind(new InetSocketAddress(InetAddress.getByName("localhost"), 10086));
        Node node = new Node("test_node", UUID.randomUUID().toString(), "localhost", "wangqi", 10086, Collections.emptyMap());
        Thread.sleep(1000 * 3);
        TcpChannel connnect = transport.connnect(node);
        Thread.sleep(1000 * 3);
        connnect.sendMessage("hhhhh".getBytes());
    }
}
