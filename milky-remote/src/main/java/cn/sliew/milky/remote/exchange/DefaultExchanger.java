package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.Node;
import cn.sliew.milky.remote.transport.TcpChannel;
import cn.sliew.milky.remote.transport.Transport;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class DefaultExchanger implements Exchanger {

    private final OutboundHandler outboundHandler;
    private final InboundHandler inboundHandler;

    private final ResponseHandlerRegistry responseHandlers = new ResponseHandlerRegistry();
    private final RequestHandlerRegistry requestHandlers = new RequestHandlerRegistry();

    private Transport transport;


    public DefaultExchanger() {
        this.outboundHandler = null;
        this.inboundHandler = null;
    }

    @Override
    public List<TcpChannel> openConnection(Node node, ConnectionProfile profile, Dispatcher dispatcher) {
        //todo 创建连接，transport
        return Collections.emptyList();
    }

    @Override
    public void bindServer(Dispatcher dispatcher) {
        //todo 绑定端口，transport
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        outboundHandler.setMessageListener(listener);
        inboundHandler.setMessageListener(listener);
    }

    public void setSlowLogThreshold(Duration slowLogThreshold) {
        inboundHandler.setSlowLogThresholdMs(slowLogThreshold.toMillis());
    }

    @Override
    public RequestHandlerRegistry getRequestHandlers() {
        return requestHandlers;
    }

    @Override
    public ResponseHandlerRegistry getResponseHandlers() {
        return responseHandlers;
    }
}
