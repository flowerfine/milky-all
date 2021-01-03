package cn.sliew.milky.transport.backup;

import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.transport.ActionListener;
import cn.sliew.milky.transport.Node;
import cn.sliew.milky.transport.TcpChannel;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class TcpTransport implements Transport {

    private static final Logger logger = LoggerFactory.getLogger(cn.sliew.milky.transport.TcpTransport.class);

    // this lock is here to make sure we close this transport and disconnect all the client nodes
    // connections while no connect operations is going on
    private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * server channels
     */
    private final Set<TcpChannel> serverChannels = Sets.newConcurrentHashSet();
    private List<InetSocketAddress> boundAddresses = new ArrayList<>();

    /**
     * client channels
     */
    final List<TcpChannel> channels = new ArrayList<>();


    @Override
    public TcpChannel connect(Node node, ActionListener<TcpChannel> listener) {
        try {
            TcpChannel channel = connect(node);
            channels.add(channel);
            ChannelConnectListener channelConnectListener = new ChannelConnectListener(node, channel, listener);
            // todo timeout
            // 处理连接超时问题
            scheduledExecutorService.schedule(() -> channelConnectListener.onTimeout(), 1000L, TimeUnit.MILLISECONDS);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initiate a single tcp socket channel.
     *
     * @param node for the initiated connection
     * @return the pending connection
     * @throws IOException if an I/O exception occurs while opening the channel
     */
    protected abstract TcpChannel connect(Node node) throws IOException;

    @Override
    public void bind(ServerSettings settings) {
        closeLock.writeLock().lock();
        try {
            TcpChannel channel = bind(new InetSocketAddress(InetAddress.getByName(settings.bindHosts), settings.port));
            serverChannels.add(channel);
            boundAddresses.add(channel.getLocalAddress());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeLock.writeLock().unlock();
        }
    }

    protected abstract TcpChannel bind(InetSocketAddress address) throws IOException;
}
