package cn.sliew.milky.common.release;

import java.io.Closeable;

public interface Releasable extends Closeable {

    @Override
    void close();
}
