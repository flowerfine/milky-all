package cn.sliew.milky.transport;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultConnectionManager implements ConnectionManager {

    private ConcurrentMap<Node, Connection> connectedNodes = new ConcurrentHashMap<>();
    private TcpTransport transport;

    @Override
    public Connection connect(Node node) {
        return transport.connect(node, null);
    }

    @Override
    public void disconnect(Node node) {
        Connection connection = connectedNodes.get(node);
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public boolean isConnected(Node node) {
        Connection connection = connectedNodes.get(node);
        if (connection != null) {
            return connection.isClosed();
        }
        return false;
    }

    @Override
    public Connection getConnection(Node node) {
        return connectedNodes.get(node);
    }

    @Override
    public int size() {
        return connectedNodes.size();
    }

    @Override
    public void addListener(ConnectionListener listener) {

    }

    @Override
    public void removeListener(ConnectionListener listener) {

    }

    @Override
    public void close() throws IOException {
        connectedNodes.values().forEach(Connection::close);
    }
}
