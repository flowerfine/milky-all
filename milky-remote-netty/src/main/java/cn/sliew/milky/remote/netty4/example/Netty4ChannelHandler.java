package cn.sliew.milky.remote.netty4.example;

import cn.sliew.milky.remote.transport.ChannelListener;
import cn.sliew.milky.remote.transport.TcpChannel;
import io.netty.buffer.ByteBuf;

public class Netty4ChannelHandler implements ChannelListener<TcpChannel> {

    @Override
    public void connected(TcpChannel channel) {
    }

    @Override
    public void disconnected(TcpChannel channel) {
    }

    @Override
    public void sent(TcpChannel channel, Object message) {
//        System.out.println("发送消息: " + message);
    }

    @Override
    public void received(TcpChannel channel, Object message) {
        ByteBuf byteBuf = (ByteBuf) message;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        System.out.println("收到消息: "+new String(bytes));
        byteBuf.release();
    }

    @Override
    public void caught(TcpChannel channel, Throwable exception) {
        exception.printStackTrace();
    }
}
