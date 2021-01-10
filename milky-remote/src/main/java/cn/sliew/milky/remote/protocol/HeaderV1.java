package cn.sliew.milky.remote.protocol;

import java.util.Map;

public class HeaderV1 {

    private final MessageHandleType messageHandleType;

    private final MessageType messageType;

    private final CodecId codecId;

    private final EncryptionType encryptionType;

    private final long requestId;

    private final long timeout;

    private final int responseStatus;

    private final Map<String, String> headers;

    private final String action;

    private final int networkMessageSize;

    public HeaderV1(MessageHandleType messageHandleType, MessageType messageType, CodecId codecId,
                    EncryptionType encryptionType, long requestId, long timeout, int responseStatus,
                    Map<String, String> headers, String action, int networkMessageSize) {
        this.messageHandleType = messageHandleType;
        this.messageType = messageType;
        this.codecId = codecId;
        this.encryptionType = encryptionType;
        this.requestId = requestId;
        this.timeout = timeout;
        this.responseStatus = responseStatus;
        this.headers = headers;
        this.action = action;
        this.networkMessageSize = networkMessageSize;
    }
}
