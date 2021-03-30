package cn.sliew.milky.log;

public class StdOutLoggerFactory extends LoggerFactory {

    public static final LoggerFactory INSTANCE = new StdOutLoggerFactory();

    @Override
    protected Logger newInstance(String name) {
        return new StdOutLogger(name);
    }
}
