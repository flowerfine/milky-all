package cn.sliew.milky.common;

public interface Ticker {

    /**
     * Returns the number of nanoseconds elapsed since this ticker's fixed point of reference.
     *
     * @return the number of nanoseconds elapsed since this ticker's fixed point of reference
     */
    long read();

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
