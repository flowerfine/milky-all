package cn.sliew.milky.transport.exchange;

public interface TransportResponseHandler<T extends TransportResponse> {

    void handleResponse(T response);

    void handleException(Exception exp);
}