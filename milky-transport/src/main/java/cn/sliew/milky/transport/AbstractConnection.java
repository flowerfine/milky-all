package cn.sliew.milky.transport;

import cn.sliew.milky.concurrent.CompletableContext;

public abstract class AbstractConnection implements Connection {

    private final CompletableContext<Void> closeContext = new CompletableContext<>();

    @Override
    public boolean isClosed() {
        return closeContext.isDone();
    }

    @Override
    public void close() {
        closeContext.complete(null);
    }
}
