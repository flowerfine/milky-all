package cn.sliew.milky.remote.peer;

import java.net.InetSocketAddress;
import java.util.Optional;

public class ShutdownCommand {

    private final InetSocketAddress localAddress;
    private final InetSocketAddress remoteAddress;

    private final Optional<Throwable> cause;

    private final String reason;

    public ShutdownCommand(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause, String reason) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.cause = Optional.ofNullable(cause);
        this.reason = reason;
    }
}
