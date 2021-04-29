package cn.sliew.milky.common.model.dto;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private static final long serialVersionUID = 5359808875847456900L;

    /**
     * response status.
     * true: success, false: failure.
     */
    private boolean success = false;

    /**
     * response message, such as failure message.
     */
    private String message;

    /**
     * reponse code.
     */
    private long code;

    /**
     * true enable exception response retry, false otherwise disable exception response retry.
     * most time client is responsible for fault tolerate such as timeout, retry, circuitbreaker,
     * but development experience indicat that server often knows more about service performance and
     * suggests how to tune fault tolerate settings. server provides suggestions for client to
     * prevent retry disaster.
     */
    private boolean retryable = false;

    /**
     * total size of data. userful for page request.
     */
    private long totalSize;

    /**
     * true enable next page request, false otherwise indicats no more data.
     */
    private boolean hasNext;

    /**
     * server timestamp when receive reqeust.
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * response cost.
     */
    private long cost;

    /**
     * response data.
     */
    private T data;
}
