package cn.sliew.milky.transport;

import java.util.Map;

public class Node {

    private final TransportAddress address;
    private final Map<String, String> attributes;

    public Node(TransportAddress address, Map<String, String> attributes) {
        this.address = address;
        this.attributes = attributes;
    }
}
