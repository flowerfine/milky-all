package cn.sliew.milky.transport;

public interface ConnectionListener {

    /**
     * Called once a connection was opened
     *
     * @param connection the connection
     */
    default void onConnectionOpened(Connection connection) {
    }

    /**
     * Called once a connection ws closed.
     *
     * @param connection the closed connection
     */
    default void onConnectionClosed(Connection connection) {
    }

    /**
     * Called once a node connection is opened and registered.
     */
    default void onNodeConnected(Node node) {
    }

    /**
     * Called once a node connection is closed and unregistered.
     */
    default void onNodeDisconnected(Node node) {
    }
}
