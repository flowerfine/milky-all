package cn.sliew.milky.concurrent;

public enum Futures {
    ;

    public <V> Promise<V> newPromise() {
        return new DefaultPromise<V>();
    }

    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new DefaultProgressivePromise<V>();
    }

    public <V> Future<V> newSucceededFuture(V result) {
        return new SucceededFuture<V>(result);
    }

    public <V> Future<V> newFailedFuture(Throwable cause) {
        return new FailedFuture<>(cause);
    }
}
