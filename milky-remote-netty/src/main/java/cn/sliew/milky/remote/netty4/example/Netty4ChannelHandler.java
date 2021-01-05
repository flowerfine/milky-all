package cn.sliew.milky.remote.netty4.example;

import cn.sliew.milky.remote.transport.ChannelListener;
import cn.sliew.milky.remote.transport.TcpChannel;

public class Netty4ChannelHandler implements ChannelListener<TcpChannel> {

    @Override
    public void connected(TcpChannel channel) {

    }

    @Override
    public void disconnected(TcpChannel channel) {

    }

    @Override
    public void sent(TcpChannel channel, Object message) {

    }

    @Override
    public void received(TcpChannel channel, Object message) {
        System.out.println(message);
    }

    @Override
    public void caught(TcpChannel channel, Throwable exception) {

    }
}
