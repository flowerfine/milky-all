package cn.sliew.milky.transport.exchange;

import cn.sliew.milky.transport.TransportResponse;

public interface TransportResponseHandler<T extends TransportResponse> {

    void handleResponse(T response);

    void handleException(Exception exp);
}