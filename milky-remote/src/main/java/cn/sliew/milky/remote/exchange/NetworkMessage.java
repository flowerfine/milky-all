package cn.sliew.milky.remote.exchange;

public abstract class NetworkMessage {

    protected final long requestId;
    protected final byte status;

    NetworkMessage(byte status, long requestId) {
        this.requestId = requestId;
        this.status = status;
    }
}
