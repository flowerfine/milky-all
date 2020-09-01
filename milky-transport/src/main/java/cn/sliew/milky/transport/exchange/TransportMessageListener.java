package cn.sliew.milky.transport.exchange;

public interface TransportMessageListener {

    /**
     * Called for every request sent to a server after the request has been passed to the underlying network implementation
     *
     * @param requestId the internal request id
     * @param request   the actual request
     */
    default void onRequestSent(long requestId, TransportRequest request) {
    }

    /**
     * Called once a request is received
     *
     * @param requestId the internal request ID
     */
    default void onRequestReceived(long requestId) {
    }

    /***
     * Called for every failed action response after the response has been passed to the underlying network implementation.
     * @param requestId the request ID (unique per client)
     * @param action the request action
     * @param error the error sent back to the caller
     */
    default void onResponseSent(long requestId, String action, Exception error) {
    }

    /**
     * Called for every action response sent after the response has been passed to the underlying network implementation.
     *
     * @param requestId the request ID (unique per client)
     * @param response  the response send
     */
    default void onResponseSent(long requestId, TransportResponse response) {
    }

    /**
     * Called for every response received
     *
     * @param requestId the request id for this reponse
     * @param context   the response context or null if the context was already processed ie. due to a timeout.
     */
    default void onResponseReceived(long requestId, ResponseContext context) {
    }
}
