package cn.sliew.milky.remote.protocol;

public enum MessageHandleType {

    REQUEST((byte) 1, "request"),
    RESPONSE((byte) 2, "response"),
    ONEWAY((byte) 3, "one way, not require response from remote"),
    ;

    private byte type;
    private String desc;

    MessageHandleType(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MessageHandleType valueOf(byte type) {
        switch (type) {
            case 1:
                return REQUEST;
            case 2:
                return RESPONSE;
            case 3:
                return ONEWAY;
            default:
                throw new IllegalArgumentException("Unknown transport message handle type: " + type);
        }
    }
}
