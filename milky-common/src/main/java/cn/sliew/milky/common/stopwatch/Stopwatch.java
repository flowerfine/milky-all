package cn.sliew.milky.common.stopwatch;

import java.time.Duration;

public final class Stopwatch {


  private final Ticker ticker;
  /**
   * Is the stop watch currently running?
   */
  private boolean running;
  /**
   * Start time of the current Stopwatch
   */
  private long startTimeNS;
  private long elapsedNanos;

  Stopwatch() {
    this.ticker = Tickers.systemTicker();
  }

  Stopwatch(Ticker ticker) {
    this.ticker = checkNotNull(ticker, "ticker");
  }

  /**
   * Returns {@code true} if {@link #start()} has been called on this stopwatch, and {@link #stop()}
   * has not been called since the last call to {@code start()}.
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * Starts the stopwatch.
   *
   * @return this {@code Stopwatch} instance
   * @throws IllegalStateException if the stopwatch is already running.
   */
  public Stopwatch start() {
    checkState(!running, "This stopwatch is already running.");
    running = true;
    startTimeNS = ticker.read();
    return this;
  }

  /**
   * Stops the stopwatch. Future reads will return the fixed duration that had elapsed up to this
   * point.
   *
   * @return this {@code Stopwatch} instance
   * @throws IllegalStateException if the stopwatch is already stopped.
   */
  public Stopwatch stop() {
    long tick = ticker.read();
    checkState(running, "This stopwatch is already stopped.");
    running = false;
    elapsedNanos += tick - startTimeNS;
    return this;
  }

  /**
   * Sets the elapsed time for this stopwatch to zero, and places it in a stopped state.
   *
   * @return this {@code Stopwatch} instance
   */
  public Stopwatch reset() {
    elapsedNanos = 0;
    running = false;
    return this;
  }

  private long elapsedNanos() {
    return running ? ticker.read() - startTimeNS + elapsedNanos : elapsedNanos;
  }

  /**
   * Returns the current elapsed time shown on this stopwatch as a {@link Duration}.
   */
  public Duration elapsed() {
    return Duration.ofNanos(elapsedNanos());
  }
}
