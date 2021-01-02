package cn.sliew.milky.transport;

import cn.sliew.milky.transport.exchange.ConnectionProfile;
import cn.sliew.milky.transport.exchange.RequestContext;
import cn.sliew.milky.transport.exchange.TransportRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface Transport {

    /**
     * The address the transport is bound on.
     * transport绑定的地址
     */
    InetSocketAddress boundAddress();

    /**
     * Opens a new connection to the given node.
     */
    Connection connect(Node node, ConnectionProfile profile, ActionListener<Connection> listener);

    void bind(InetAddress hostAddress, String port);

    void addMessageListener(TransportMessageListener listener);

    boolean removeMessageListener(TransportMessageListener listener);

    // request和response的处理，response的同步转异步

    /**
     * Registers a new request handler
     * 注册一个新的请求handler
     */
    <Request extends TransportRequest> void registerRequestHandler(RequestContext<Request> request);

    /**
     * Returns the registered request handler registry for the given action or <code>null</code> if it's not registered
     * 返回指定的action注册的请求handler注册表，如果没有注册返回null。
     *
     * @param action the action to look up 需要查找的action
     */
    RequestContext<? extends TransportRequest> getRequestHandler(String action);
}