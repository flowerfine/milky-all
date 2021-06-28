package cn.sliew.milky.cache.lettuce;

import java.io.Closeable;

public interface LettuceConnectionFactory {

    LettuceConnection getConnection();

    interface LettuceConnection extends Closeable {

        LettuceCommandsWrapper sync();

        LettuceAsyncCommandsWrapper async();
    }
}
