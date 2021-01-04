package cn.sliew.milky.remote.transport;

import java.util.Map;

/**
 * 表示一个节点
 */
public class Node {

    private final String nodeName;
    /**
     * 临时id，考虑到节点重启等，临时id会发生改变
     * ephemeralId和节点的生命周期绑定
     */
    private final String ephemeralId;
    private final String hostAddress;
    private final String hostName;
    private final int port;
    // todo 考虑使用netty的AttributeMap取代attributes
    private final Map<String, String> attributes;

    public Node(String nodeName, String ephemeralId, String hostAddress, String hostName, Map<String, String> attributes) {
        this.nodeName = nodeName;
        this.ephemeralId = ephemeralId;
        this.hostAddress = hostAddress;
        this.hostName = hostName;
        this.attributes = attributes;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getEphemeralId() {
        return ephemeralId;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}