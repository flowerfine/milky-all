package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.ActionListener;
import cn.sliew.milky.remote.transport.Node;

import java.util.Set;

public interface ConnectionManager {

    void openConnection(Node node, ConnectionProfile connectionProfile, ActionListener<Connection> listener);

    boolean connected(Node node);

    Connection getConnection(Node node);

    void stopConnection(Node node);

    Set<Node> getConnectedNodes();
}