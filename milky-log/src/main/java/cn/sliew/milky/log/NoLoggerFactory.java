package cn.sliew.milky.log;

public class NoLoggerFactory extends LoggerFactory {

    public static final LoggerFactory INSTANCE = new NoLoggerFactory();

    @Override
    protected Logger newInstance(String name) {
        return new NoLogger(name);
    }
}
