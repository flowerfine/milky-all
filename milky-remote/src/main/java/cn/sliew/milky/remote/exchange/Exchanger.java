package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.Node;
import cn.sliew.milky.remote.transport.TcpChannel;

import java.time.Duration;
import java.util.List;

public interface Exchanger {

    List<TcpChannel> openConnection(Node node, ConnectionProfile profile);

    void bindServer();

    void setMessageListener(MessageListener listener);

    default void setSlowLogThreshold(Duration slowLogThreshold) {
    }


    RequestHandlerRegistry getRequestHandlers();

    ResponseHandlerRegistry getResponseHandlers();
}
