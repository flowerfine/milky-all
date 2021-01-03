package cn.sliew.milky.remote.exchange;

import cn.sliew.milky.remote.transport.ActionListener;
import cn.sliew.milky.remote.transport.Node;

import java.time.Duration;

public interface Exchanger {

    void openConnection(Node node, ConnectionProfile profile, ActionListener<Connection> listener);


    void setMessageListener(MessageListener listener);

    default void setSlowLogThreshold(Duration slowLogThreshold) {

    }
}
