package cn.sliew.milky.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Node {

    private final TransportAddress address;
    private final Map<String, String> attributes;

    public Node(TransportAddress address, Map<String, String> attributes) {
        this.address = address;
        this.attributes = attributes;
    }

    private static final Logger log = LoggerFactory.getLogger(Node.class);

    public static void main(String[] args) {
        log.info("hhh");
    }
}
