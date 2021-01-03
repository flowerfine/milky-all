package cn.sliew.milky.transport;

import java.io.Closeable;
import java.io.IOException;

public interface Channel extends Closeable {

    /**
     * 通常channel的关闭是异步的，因此不能捕获{@link IOException}，这里对方法
     * 进行重写，去除了{@link IOException}异常声明，并添加了关闭监听器，处理关闭
     * 异常。
     */
    @Override
    void close();

    void addCloseListener(ActionListener<Void> listener);

    /**
     * Indicates whether a channel is currently open
     *
     * @return boolean indicating if channel is open
     */
    boolean isOpen();
}
