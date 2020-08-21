package cn.sliew.milky.common.stopwatch;

/**
 * 因为jdk提供的{@link java.time.Clock}只能支持毫秒级的时间，所以单独定义了Ticker提供
 * 纳秒级的时间
 */
public interface Ticker {

    /**
     * Returns the number of nanoseconds elapsed since this ticker's fixed point of reference.
     *
     * @return the number of nanoseconds elapsed since this ticker's fixed point of reference
     */
    long read();
}
