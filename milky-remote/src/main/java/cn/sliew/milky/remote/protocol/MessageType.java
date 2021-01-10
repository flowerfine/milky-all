package cn.sliew.milky.remote.protocol;

public enum MessageType {

    PAYLOAD((byte) 1, "message payload"),
    PING((byte) 2, "ping"),
    ;

    private byte type;
    private String desc;

    MessageType(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MessageType valueOf(byte type) {
        switch (type) {
            case 1:
                return PAYLOAD;
            case 2:
                return PING;
            default:
                throw new IllegalArgumentException("Unknown transport message type: " + type);
        }
    }
}
