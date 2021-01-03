package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.TransportRequest;
import cn.sliew.milky.remote.TransportResponse;
import cn.sliew.milky.remote.transport.ActionListener;
import cn.sliew.milky.remote.transport.Node;
import cn.sliew.milky.remote.transport.TcpChannel;

import java.io.IOException;

public class OutboundHandler {

    private volatile MessageListener messageListener = MessageListener.NOOP_LISTENER;

    public void setMessageListener(MessageListener listener) {
        if (messageListener == MessageListener.NOOP_LISTENER) {
            messageListener = listener;
        } else {
            throw new IllegalStateException("Cannot set message listener twice");
        }
    }

    /**
     * Sends the request to the given channel. This method should be used to send {@link TransportRequest}
     * objects back to the caller.
     */
    void sendRequest(Node node, TcpChannel channel, long requestId, String action, TransportRequest request) throws IOException {
        OutboundMessage message = null;
        ActionListener<Void> listener = ActionListener.wrap(() -> messageListener.onRequestSent(node, requestId, action, request));
        sendMessage(channel, message, listener);
    }

    /**
     * Sends the response to the given channel. This method should be used to send {@link TransportResponse}
     * objects back to the caller.
     *
     * @see #sendErrorResponse(TcpChannel, long, String, Exception) for sending error responses
     */
    void sendResponse(TcpChannel channel, long requestId, String action, TransportResponse response) throws IOException {
        OutboundMessage message = null;
        ActionListener<Void> listener = ActionListener.wrap(() -> messageListener.onResponseSent(requestId, action, response));
        sendMessage(channel, message, listener);
    }

    /**
     * Sends back an error response to the caller via the given channel
     */
    void sendErrorResponse(TcpChannel channel, long requestId, String action, Exception error) throws IOException {
        OutboundMessage message = null;
        ActionListener<Void> listener = ActionListener.wrap(() -> messageListener.onResponseSent(requestId, action, error));
        sendMessage(channel, message, listener);
    }

    private void sendMessage(TcpChannel channel, OutboundMessage networkMessage, ActionListener<Void> listener) throws IOException {
        channel.sendMessage(null);
    }
}
