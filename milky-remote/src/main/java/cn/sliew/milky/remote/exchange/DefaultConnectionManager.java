package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.ActionListener;
import cn.sliew.milky.remote.transport.Node;
import cn.sliew.milky.remote.transport.TcpChannel;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class DefaultConnectionManager implements ConnectionManager {

    private final ConcurrentMap<Node, Connection> connectedNodes = Maps.newConcurrentMap();
    private final Exchanger exchanger;
    private final ConnectionProfile defaultProfile;

    public DefaultConnectionManager(ConnectionProfile connectionProfile, Exchanger exchanger) {
        this.exchanger = exchanger;
        this.defaultProfile = connectionProfile;
    }

    @Override
    public void openConnection(Node node, ConnectionProfile connectionProfile, ActionListener<Connection> listener) {
        //fixme dispatcher
        List<TcpChannel> tcpChannels = exchanger.openConnection(node, connectionProfile, null);
        //todo 创建connection
    }

    @Override
    public boolean connected(Node node) {
        return connectedNodes.containsKey(node);
    }

    @Override
    public Connection getConnection(Node node) {
        Connection connection = connectedNodes.get(node);
        if (connection == null) {
            throw new RuntimeException("Node not connected");
        }
        return connection;
    }

    @Override
    public void stopConnection(Node node) {
        Connection nodeChannels = connectedNodes.remove(node);
        if (nodeChannels != null) {
            nodeChannels.close();
        }
    }

    @Override
    public Set<Node> getConnectedNodes() {
        return Collections.unmodifiableSet(connectedNodes.keySet());
    }
}