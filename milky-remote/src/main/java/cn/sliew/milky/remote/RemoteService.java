package cn.sliew.milky.remote;

import cn.sliew.milky.remote.exchange.Connection;
import cn.sliew.milky.remote.exchange.ConnectionManager;
import cn.sliew.milky.remote.exchange.ResponseContext;
import cn.sliew.milky.remote.exchange.ResponseHandlerRegistry;
import cn.sliew.milky.remote.transport.Node;

public class RemoteService {

    private ConnectionManager connectionManager;
    private ResponseHandlerRegistry responseHandlers;

    public <T extends TransportResponse> void sendRequest(Node node, String action, TransportRequest request, TransportResponseHandler<T> handler) {
        Connection connection;
        try {
            connection = connectionManager.getConnection(node);
        } catch (Exception ex) {
            // the caller might not handle this so we invoke the handler
            handler.handleException(ex);
            return;
        }
        sendRequest(connection, action, request, handler);
    }

    /**
     * Sends a request on the specified connection. If there is a failure sending the request, the specified handler is invoked.
     *
     * @param connection the connection to send the request on
     * @param action     the name of the action
     * @param request    the request
     * @param handler    the response handler
     * @param <T>        the type of the transport response
     */
    public final <T extends TransportResponse> void sendRequest(Connection connection, String action, TransportRequest request, TransportResponseHandler<T> handler) {
        try {
            sendRequestInternal(connection, action, request, handler);
        } catch (Exception ex) {
            // the caller might not handle this so we invoke the handler
            handler.handleException(ex);
        }
    }

    private <T extends TransportResponse> void sendRequestInternal(Connection connection, String action, TransportRequest request, TransportResponseHandler<T> handler) {
        if (connection == null) {
            throw new IllegalStateException("can't send request to a null connection");
        }
        long requestId = responseHandlers.add(new ResponseContext(handler, connection, action));
        try {
            connection.sendRequest(requestId, action, request); // local node optimization happens upstream
        } catch (final Exception e) {
            // usually happen either because we failed to connect to the node
            // or because we failed serializing the message
        }
    }
}
