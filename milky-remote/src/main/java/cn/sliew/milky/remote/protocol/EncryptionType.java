package cn.sliew.milky.remote.protocol;

public enum EncryptionType {

    NONE((byte) 0, "none encryption"),
    CRC((byte) 1, "crc"),
    ;

    private byte type;
    private String desc;

    EncryptionType(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static EncryptionType valueOf(byte type) {
        switch (type) {
            case 0:
                return NONE;
            case 1:
                return CRC;
            default:
                throw new IllegalArgumentException("Unknown encryption type: " + type);
        }
    }
}
