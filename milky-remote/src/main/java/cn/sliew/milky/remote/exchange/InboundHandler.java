package cn.sliew.milky.remote.exchange;

public class InboundHandler {

    private volatile MessageListener messageListener = MessageListener.NOOP_LISTENER;

    private volatile long slowLogThresholdMs = Long.MAX_VALUE;

    public void setMessageListener(MessageListener listener) {
        if (messageListener == MessageListener.NOOP_LISTENER) {
            messageListener = listener;
        } else {
            throw new IllegalStateException("Cannot set message listener twice");
        }
    }

    public void setSlowLogThresholdMs(long slowLogThresholdMs) {
        this.slowLogThresholdMs = slowLogThresholdMs;
    }
}
