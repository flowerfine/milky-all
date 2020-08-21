package cn.sliew.milky.common.watchdog;

public class Noop implements ThreadWatchdog {

    static final Noop INSTANCE = new Noop();

    @Override
    public void register() {

    }

    @Override
    public long maxExecutionTimeInMillis() {
        return Long.MAX_VALUE;
    }

    @Override
    public void unregister() {

    }
}
