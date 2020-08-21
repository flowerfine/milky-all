package cn.sliew.milky.common.stopwatch;

public enum SystemTicker implements Ticker {
    INSTANCE;

    @Override
    public long read() {
        return System.nanoTime();
    }
}
