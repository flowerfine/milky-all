package cn.sliew.milky.transport.backup;

import cn.sliew.milky.transport.ActionListener;
import cn.sliew.milky.transport.Node;
import cn.sliew.milky.transport.TcpChannel;

public interface Transport {

    TcpChannel connect(Node node, ActionListener<TcpChannel> listener);

    void bind(ServerSettings settings);
}