package cn.sliew.milky.remote;

public interface TransportResponseHandler<T extends TransportResponse> {

    void handleResponse(T response);

    void handleException(Exception exp);

    String executor();
}