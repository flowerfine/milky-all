package cn.sliew.milky.transport;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Arrays;

public class InetAddresses {

    private static int IPV4_PART_COUNT = 4;
    private static int IPV6_PART_COUNT = 8;

    /**
     * Returns the string representation of an {@link InetAddress} suitable
     * for inclusion in a URI.
     *
     * <p>For IPv4 addresses, this is identical to
     * {@link InetAddress#getHostAddress()}, but for IPv6 addresses it
     * compresses zeroes and surrounds the text with square brackets; for example
     * {@code "[2001:db8::1]"}.
     *
     * <p>Per section 3.2.2 of
     * <a target="_parent"
     * href="http://tools.ietf.org/html/rfc3986#section-3.2.2"
     * >http://tools.ietf.org/html/rfc3986</a>,
     * a URI containing an IPv6 string literal is of the form
     * {@code "http://[2001:db8::1]:8888/index.html"}.
     *
     * <p>Use of either {@link InetAddresses#toAddrString},
     * {@link InetAddress#getHostAddress()}, or this method is recommended over
     * {@link InetAddress#toString()} when an IP address string literal is
     * desired.  This is because {@link InetAddress#toString()} prints the
     * hostname and the IP address string joined by a "/".
     *
     * @param ip {@link InetAddress} to be converted to URI string literal
     * @return {@code String} containing URI-safe string literal
     */
    public static String toUriString(InetAddress ip) {
        if (ip instanceof Inet6Address) {
            return "[" + toAddrString(ip) + "]";
        }
        return toAddrString(ip);
    }

    /**
     * Returns the string representation of an {@link InetAddress}.
     *
     * <p>For IPv4 addresses, this is identical to
     * {@link InetAddress#getHostAddress()}, but for IPv6 addresses, the output
     * follows <a href="http://tools.ietf.org/html/rfc5952">RFC 5952</a>
     * section 4.  The main difference is that this method uses "::" for zero
     * compression, while Java's version uses the uncompressed form.
     *
     * <p>This method uses hexadecimal for all IPv6 addresses, including
     * IPv4-mapped IPv6 addresses such as "::c000:201".  The output does not
     * include a Scope ID.
     *
     * @param ip {@link InetAddress} to be converted to an address string
     * @return {@code String} containing the text-formatted IP address
     * @since 10.0
     */
    public static String toAddrString(InetAddress ip) {
        if (ip == null) {
            throw new NullPointerException("ip");
        }
        if (ip instanceof Inet4Address) {
            // For IPv4, Java's formatting is good enough.
            byte[] bytes = ip.getAddress();
            return (bytes[0] & 0xff) + "." + (bytes[1] & 0xff) + "." + (bytes[2] & 0xff) + "." + (bytes[3] & 0xff);
        }
        if (!(ip instanceof Inet6Address)) {
            throw new IllegalArgumentException("ip");
        }
        byte[] bytes = ip.getAddress();
        int[] hextets = new int[IPV6_PART_COUNT];
        for (int i = 0; i < hextets.length; i++) {
            hextets[i] = (bytes[2 * i] & 255) << 8 | bytes[2 * i + 1] & 255;
        }
        compressLongestRunOfZeroes(hextets);
        return hextetsToIPv6String(hextets);
    }

    /**
     * Identify and mark the longest run of zeroes in an IPv6 address.
     *
     * <p>Only runs of two or more hextets are considered.  In case of a tie, the
     * leftmost run wins.  If a qualifying run is found, its hextets are replaced
     * by the sentinel value -1.
     *
     * @param hextets {@code int[]} mutable array of eight 16-bit hextets
     */
    private static void compressLongestRunOfZeroes(int[] hextets) {
        int bestRunStart = -1;
        int bestRunLength = -1;
        int runStart = -1;
        for (int i = 0; i < hextets.length + 1; i++) {
            if (i < hextets.length && hextets[i] == 0) {
                if (runStart < 0) {
                    runStart = i;
                }
            } else if (runStart >= 0) {
                int runLength = i - runStart;
                if (runLength > bestRunLength) {
                    bestRunStart = runStart;
                    bestRunLength = runLength;
                }
                runStart = -1;
            }
        }
        if (bestRunLength >= 2) {
            Arrays.fill(hextets, bestRunStart, bestRunStart + bestRunLength, -1);
        }
    }

    /**
     * Convert a list of hextets into a human-readable IPv6 address.
     *
     * <p>In order for "::" compression to work, the input should contain negative
     * sentinel values in place of the elided zeroes.
     *
     * @param hextets {@code int[]} array of eight 16-bit hextets, or -1s
     */
    private static String hextetsToIPv6String(int[] hextets) {
        /*
         * While scanning the array, handle these state transitions:
         *   start->num => "num"     start->gap => "::"
         *   num->num   => ":num"    num->gap   => "::"
         *   gap->num   => "num"     gap->gap   => ""
         */
        StringBuilder buf = new StringBuilder(39);
        boolean lastWasNumber = false;
        for (int i = 0; i < hextets.length; i++) {
            boolean thisIsNumber = hextets[i] >= 0;
            if (thisIsNumber) {
                if (lastWasNumber) {
                    buf.append(':');
                }
                buf.append(Integer.toHexString(hextets[i]));
            } else {
                if (i == 0 || lastWasNumber) {
                    buf.append("::");
                }
            }
            lastWasNumber = thisIsNumber;
        }
        return buf.toString();
    }

    private InetAddresses() {
        throw new AssertionError();
    }
}
