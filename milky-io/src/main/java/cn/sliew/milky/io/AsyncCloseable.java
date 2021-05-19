package cn.sliew.milky.io;

import java.io.Closeable;
import java.util.function.BiConsumer;

/**
 * Async closeable interface.
 * In some case, resource close is an async operation and not elegantly capture
 * {@link java.io.IOException} within {@code #close} method. {@code AsyncCloseable}
 * try to add listener handle close failure and provide utility indicate close status.
 *
 * But it isn't a good idea close streams and resources within try with resource block.
 */
public interface AsyncCloseable extends Closeable {

    /**
     * Async close streams and resources.
     */
    @Override
    void close();

    /**
     * Return true when streams and resources close completely, false otherwise.
     */
    boolean isClosed();

    /**
     * Add failure listener for handling close exception.
     * @param closeListener failure listener.
     */
    void addCloseListener(BiConsumer<Void, Exception> closeListener);
}
