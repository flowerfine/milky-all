package cn.sliew.milky.remote.transport;

import java.io.Closeable;
import java.io.IOException;

/**
 * fixme 如何区分 Tcp 和 http 级的 channel，或者说能否为二者提供统一的抽象？
 */
public interface Channel extends Closeable {

    /**
     * Commonly {@link Channel} close operation would be asynchronous, which
     * it is not possiable capture {@link IOException}. So it is unnecessary
     * to add throws and replace it with {@link #addCloseListener(ActionListener)}.
     */
    @Override
    void close();

    /**
     * is closed.
     *
     * @return closed
     */
    boolean isClosed();

    void addCloseListener(ActionListener<Void> listener);

    /**
     * Indicates whether a channel is currently open
     *
     * @return boolean indicating if channel is open
     */
    boolean isOpen();

    /**
     * Returns {@code true} if and only if the I/O thread will perform the
     * requested write operation immediately.  Any write requests made when
     * this method returns {@code false} are queued until the I/O thread is
     * ready to process the queued write requests.
     */
    boolean isWritable();
}
