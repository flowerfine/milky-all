package cn.sliew.milky.transport.exchange;

import cn.sliew.milky.transport.Connection;
import cn.sliew.milky.transport.TransportResponse;

public interface Exchange {

    //发送请求，处理请求，发送响应，处理响应


    void sendResponse(TransportResponse response);

    void sendResponse(Exception exception);

    <T extends TransportResponse> void sendRequest(Connection connection, TransportRequest request, TransportResponseHandler<T> handler);

    /**
     * This method handles the message receive part for both request and responses
     */
    void messageReceived(Object message, Connection connection);
}
