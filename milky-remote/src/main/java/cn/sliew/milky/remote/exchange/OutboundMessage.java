package cn.sliew.milky.remote.exchange;

/**
 * outbound message 分为request和response
 */
abstract class OutboundMessage extends NetworkMessage {

    public OutboundMessage(byte status, long requestId) {
        super(status, requestId);
    }
}