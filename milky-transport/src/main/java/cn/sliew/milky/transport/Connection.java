package cn.sliew.milky.transport;

import java.io.Closeable;

/**
 * 是channel的封装，包含多个channel
 */
public interface Connection extends Closeable {

    Channel getChannel();
}
