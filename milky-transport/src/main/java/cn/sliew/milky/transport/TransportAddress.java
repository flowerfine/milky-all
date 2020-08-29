package cn.sliew.milky.transport;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;

public final class TransportAddress {

    private final InetSocketAddress address;

    public TransportAddress(InetAddress address, int port) {
        this(new InetSocketAddress(address, port));
    }

    public TransportAddress(InetSocketAddress address) {
        if (address == null) {
            throw new IllegalArgumentException("InetSocketAddress must not be null");
        }
        if (address.getAddress() == null) {
            throw new IllegalArgumentException("Address must be resolved but wasn't - InetSocketAddress#getAddress() returned null");
        }
        this.address = address;
    }

    /**
     * Returns a string representation of the enclosed {@link InetSocketAddress}
     *
     * @see #format(InetAddress, int)
     */
    public String getAddress() {
        return format(address.getAddress(), -1);
    }

    /**
     * Returns the addresses port
     */
    public int getPort() {
        return address.getPort();
    }

    /**
     * Returns the enclosed {@link InetSocketAddress}
     */
    public InetSocketAddress address() {
        return this.address;
    }

    // note, we don't validate port, because we only allow InetSocketAddress
    private static String format(InetAddress address, int port) {
        Objects.requireNonNull(address);

        StringBuilder builder = new StringBuilder();

        if (port != -1 && address instanceof Inet6Address) {
            builder.append(InetAddresses.toUriString(address));
        } else {
            builder.append(InetAddresses.toAddrString(address));
        }

        if (port != -1) {
            builder.append(':');
            builder.append(port);
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportAddress address1 = (TransportAddress) o;
        return address.equals(address1.address);
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }

    @Override
    public String toString() {
        return format(address.getAddress(), address.getPort());
    }
}
