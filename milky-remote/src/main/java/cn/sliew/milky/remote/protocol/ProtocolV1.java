package cn.sliew.milky.remote.protocol;

public class ProtocolV1 implements Protocol {

    public static final byte PROTOCOL_CODE       = (byte) 1;
    /** version 1, is the same with RpcProtocol */
    public static final byte PROTOCOL_VERSION_1  = (byte) 1;

    private static final int REQUEST_HEADER_LEN  = 23;
    private static final int RESPONSE_HEADER_LEN = 21;

    private final OutboundEncoder encoder;
    private final InboundDecoder decoder;

    public ProtocolV1() {
        this.encoder = new RpcCommandEncoderV2();
        this.decoder = new RpcCommandDecoderV2();
    }

    public static int getRequestHeaderLength() {
        return ProtocolV1.REQUEST_HEADER_LEN;
    }

    public static int getResponseHeaderLength() {
        return ProtocolV1.RESPONSE_HEADER_LEN;
    }


    @Override
    public OutboundEncoder getEncoder() {
        return null;
    }

    @Override
    public InboundDecoder getDecoder() {
        return null;
    }
}
