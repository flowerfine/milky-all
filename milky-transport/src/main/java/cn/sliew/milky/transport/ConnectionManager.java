package cn.sliew.milky.transport;

import java.io.Closeable;

public interface ConnectionManager extends Closeable {

    Connection connect(Node node);

    /**
     * Disconnected from the given node, if not connected, will do nothing.
     */
    void disconnect(Node node);

    /**
     * Returns {@code true} if the node is connected.
     */
    boolean isConnected(Node node);

    Connection getConnection(Node node);

    int size();


    void addListener(ConnectionListener listener);

    void removeListener(ConnectionListener listener);
}
