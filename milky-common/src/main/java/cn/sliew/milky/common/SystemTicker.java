package cn.sliew.milky.common;

public enum SystemTicker implements Ticker {
    INSTANCE;

    @Override
    public long read() {
        return System.nanoTime();
    }
}
