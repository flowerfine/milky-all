package cn.sliew.milky.transport;

/**
 * A listener for action responses or failures.
 */
public interface ActionListener<Response> {

    /**
     * Handle action response. This response may constitute a failure or a
     * success but it is up to the listener to make that decision.
     */
    void onResponse(Response response);

    /**
     * A failure caused by an exception at some phase of the task.
     */
    void onFailure(Exception e);
}
