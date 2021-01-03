package cn.sliew.milky.transport.backup;

public class ServerSettings {
    public final boolean tcpNoDelay;
    public final boolean tcpKeepAlive;
    public final boolean reuseAddress;
    public final byte sendBufferSize;
    public final byte receiveBufferSize;
    public final String bindHosts;
    public final int port;

    public ServerSettings() {
        tcpKeepAlive = true;
        tcpNoDelay = true;
        reuseAddress = true;
        sendBufferSize = (byte) 1024;
        receiveBufferSize = (byte) 1024;
        bindHosts = "127.0.0.1";
        port = 10086;
    }
}