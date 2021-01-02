package cn.sliew.milky.transport;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.transport.exchange.ConnectionProfile;
import cn.sliew.milky.transport.exchange.RequestContext;
import cn.sliew.milky.transport.exchange.TransportRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class TcpTransport implements Transport {

    private static final Logger logger = LoggerFactory.getLogger(TcpTransport.class);

    // this lock is here to make sure we close this transport and disconnect all the client nodes
    // connections while no connect operations is going on
    private final ReadWriteLock closeLock = new ReentrantReadWriteLock();

    private final String transportName;
    private final String nodeName;

    private volatile InetSocketAddress boundAddress;

    private final DelegatingTransportMessageListener messageListener = new DelegatingTransportMessageListener();
    private volatile Set<RequestContext<? extends TransportRequest>> requestContexts = Collections.emptySet();

    public TcpTransport(String transportName, String nodeName) {
        this.transportName = transportName;
        this.nodeName = nodeName;
    }

    @Override
    public InetSocketAddress boundAddress() {
        return this.boundAddress;
    }

    @Override
    public Connection connect(Node node, ConnectionProfile profile, ActionListener<Connection> listener) {
        if (node == null) {
            throw new RuntimeException("can't open connection to a null node");
        }
        closeLock.readLock().lock(); // ensure we don't open connections while we are closing
        try {
            List<TcpChannel> pendingChannels = initiateConnection(node, finalProfile, listener);
            return () -> CloseableChannel.closeChannels(pendingChannels, false);
        } finally {
            closeLock.readLock().unlock();
        }
    }

    private List<TcpChannel> initiateConnection(DiscoveryNode node, ConnectionProfile connectionProfile,
                                                ActionListener<Transport.Connection> listener) {
        int numConnections = connectionProfile.getNumConnections();
        assert numConnections > 0 : "A connection profile must be configured with at least one connection";

        final List<TcpChannel> channels = new ArrayList<>(numConnections);

        for (int i = 0; i < numConnections; ++i) {
            try {
                TcpChannel channel = initiateChannel(node);
                logger.trace(() -> new ParameterizedMessage("Tcp transport client channel opened: {}", channel));
                channels.add(channel);
            } catch (ConnectTransportException e) {
                CloseableChannel.closeChannels(channels, false);
                listener.onFailure(e);
                return channels;
            } catch (Exception e) {
                CloseableChannel.closeChannels(channels, false);
                listener.onFailure(new ConnectTransportException(node, "general node connection failure", e));
                return channels;
            }
        }

        ChannelsConnectedListener channelsConnectedListener = new ChannelsConnectedListener(node, connectionProfile, channels, listener);

        for (TcpChannel channel : channels) {
            channel.addConnectListener(channelsConnectedListener);
        }

        TimeValue connectTimeout = connectionProfile.getConnectTimeout();
        threadPool.schedule(channelsConnectedListener::onTimeout, connectTimeout, ThreadPool.Names.GENERIC);
        return channels;
    }


    @Override
    public void bind(InetAddress hostAddress, String port) {

    }

    @Override
    public void addMessageListener(TransportMessageListener listener) {
        messageListener.listeners.add(listener);
    }

    @Override
    public boolean removeMessageListener(TransportMessageListener listener) {
        return messageListener.listeners.remove(listener);
    }

    @Override
    public <Request extends TransportRequest> void registerRequestHandler(RequestContext<Request> request) {
        if (requestContexts.contains(request)) {
            throw new IllegalArgumentException("transport handlers is already registered");
        }
        HashSet<RequestContext<? extends TransportRequest>> requestHandlers = new HashSet<>(requestContexts);
        requestHandlers.add(request);
        requestContexts = Collections.unmodifiableSet(requestHandlers);
    }

    @Override
    public RequestContext<? extends TransportRequest> getRequestHandler(String action) {
        //todo requestContexts
        return null;
    }
}
