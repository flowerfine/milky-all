package cn.sliew.milky.remote.protocol;

public class TcpHeaderV1 {

    /**
     * magic code for protocol type
     * eg. dubbo, thrift, redis
     */
    public static final int PROTOCOL_SIZE = 1;

    /**
     * magic code for protocol version for protocol field update
     * eg. v1, v2...
     */
    public static final int PROTOCOL_VERSION_SIZE = 1;

    /**
     * magic code for message handle type.
     * eg. request, response, one way
     * elasticsearch use status handle message handle type、 response status、compress
     */
    public static final int MESSAGE_HANDEL_TYPE_SIZE = 1;

    /**
     * magic code for message type.
     * eg. payload, ping
     */
    public static final int MESSAGE_TYPE_SIZE = 2;

    /**
     * magic code for message type version.
     * eg. v1, v2...
     */
    public static final int MESSAGE_TYPE_VERSION_SIZE = 1;

    /**
     * request id.
     */
    public static final int REQUEST_ID_SIZE = 8;

    /**
     * magic code for serializer.
     * eg. fst, avro, jackson
     */
    public static final int CODEC_ID_SIZE = 1;

    /**
     * magic code for encryption.
     * eg. 0: none, 1: crc
     */
    public static final int ENCRYPTION_TYPE = 1;

    /**
     * crc size.
     */
    public static final int CRC_SIZE = 4;

    /**
     * request time out.
     */
    public static final int REQUEST_TIME_OUT_SIZE = 4;

    /**
     * response status.
     * eg. message, exception
     */
    public static final int RESPONSE_STATUS_SIZE = 2;

    /**
     * variable header size.
     */
    public static final int VARIABLE_HEADER_SIZE = 4;

    /**
     * variable content size.
     */
    public static final int VARIABLE_CONTENT_SIZE = 4;

    public static final int HEADER_SIZE = PROTOCOL_SIZE + PROTOCOL_VERSION_SIZE + MESSAGE_HANDEL_TYPE_SIZE +
            MESSAGE_TYPE_SIZE + MESSAGE_TYPE_VERSION_SIZE + REQUEST_ID_SIZE + CODEC_ID_SIZE + ENCRYPTION_TYPE +
            CRC_SIZE + VARIABLE_HEADER_SIZE + VARIABLE_CONTENT_SIZE;


}