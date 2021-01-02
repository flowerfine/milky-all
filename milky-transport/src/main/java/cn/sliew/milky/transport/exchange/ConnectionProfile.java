package cn.sliew.milky.transport.exchange;

import cn.sliew.milky.common.unit.TimeValue;

public class ConnectionProfile {

    private final int numConnections;
    private final TimeValue connectTimeout;
    private final TimeValue handshakeTimeout;
    private final TimeValue pingInterval;
    private final Boolean compressionEnabled;

    public ConnectionProfile(int numConnections, TimeValue connectTimeout,
                             TimeValue handshakeTimeout, TimeValue pingInterval, Boolean compressionEnabled) {
        this.numConnections = numConnections;
        this.connectTimeout = connectTimeout;
        this.handshakeTimeout = handshakeTimeout;
        this.pingInterval = pingInterval;
        this.compressionEnabled = compressionEnabled;
    }
}
