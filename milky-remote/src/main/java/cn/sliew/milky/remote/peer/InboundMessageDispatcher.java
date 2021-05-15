package cn.sliew.milky.remote.peer;

public interface InboundMessageDispatcher {

    void dispatch(Object handler, Object handlerAddress, Object message, Object context);
}
