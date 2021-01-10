package cn.sliew.milky.remote.protocol;

public enum CodecId {

    STRING((byte) 1, "xxx.getBytes(Charset.forName(\"UTF-8\"))"),
    ;

    private byte type;
    private String desc;

    CodecId(byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static CodecId valueOf(byte type) {
        switch (type) {
            case 1:
                return STRING;
            default:
                throw new IllegalArgumentException("Unknown transport codec id: " + type);
        }
    }
}
