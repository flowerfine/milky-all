package cn.sliew.milky.concurrent.thread;

import java.util.concurrent.RejectedExecutionHandler;

public interface XRejectedExecutionHandler extends RejectedExecutionHandler {

    /**
     * The number of rejected executions.
     */
    long rejected();
}