package cn.sliew.milky.common.stopwatch;

public enum Tickers {
    ;

    /**
     * Returns a ticker that reads the current time using {@link System#nanoTime}.
     *
     * @return a ticker that reads the current time using {@link System#nanoTime}
     */
    static Ticker systemTicker() {
        return SystemTicker.INSTANCE;
    }

    /**
     * Returns a ticker that always returns {@code 0}.
     *
     * @return a ticker that always returns {@code 0}
     */
    static Ticker disabledTicker() {
        return DisabledTicker.INSTANCE;
    }
}
