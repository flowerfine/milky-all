package cn.sliew.milky.remote;

public interface TransportResponseHandler<Response extends TransportResponse> {

    void handleResponse(Response response);

    void handleException(Exception exp);

    String executor();
}