package cn.sliew.milky.remote.protocol;

public class ProtocolV1 implements Protocol {

    /**
     * default protocol
     */
    public static final byte PROTOCOL_CODE = (byte) 1;
    /**
     * version 1
     */
    public static final byte PROTOCOL_VERSION_1 = (byte) 1;

    private static final int REQUEST_HEADER_LEN = 23;
    private static final int RESPONSE_HEADER_LEN = 21;

    private final OutboundEncoder encoder;
    private final InboundDecoder decoder;

    public ProtocolV1() {
        this.encoder = new OutboundEncoderV2();
        this.decoder = new InboundDecoderV1();
    }

    public static int getRequestHeaderLength() {
        return ProtocolV1.REQUEST_HEADER_LEN;
    }

    public static int getResponseHeaderLength() {
        return ProtocolV1.RESPONSE_HEADER_LEN;
    }


    @Override
    public OutboundEncoder getEncoder() {
        return this.encoder;
    }

    @Override
    public InboundDecoder getDecoder() {
        return this.decoder;
    }
}
