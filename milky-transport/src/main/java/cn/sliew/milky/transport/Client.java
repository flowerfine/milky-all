package cn.sliew.milky.transport;

public interface Client {

    /**
     * is connected.
     *
     * @return connected
     */
    boolean isConnected();

    /**
     * connect.
     */
    void connect();
}
