package cn.sliew.milky.log;

/**
 * Logger factory which creates a <a href="http://www.slf4j.org/">SLF4J</a> logger.
 */
public class Slf4jLoggerFactory extends LoggerFactory {

    @SuppressWarnings("deprecation")
    public static final LoggerFactory INSTANCE = new Slf4jLoggerFactory(true);

    Slf4jLoggerFactory(boolean failIfNOP) {
        assert failIfNOP; // Should be always called with true.
        if (org.slf4j.LoggerFactory.getILoggerFactory() instanceof
                org.slf4j.helpers.NOPLoggerFactory) {
            throw new NoClassDefFoundError("NOPLoggerFactory not supported");
        }
    }

    @Override
    public Logger newInstance(String name) {
        return wrapLogger(org.slf4j.LoggerFactory.getLogger(name));
    }

    // package-private for testing.
    static Logger wrapLogger(org.slf4j.Logger logger) {
        return logger instanceof org.slf4j.spi.LocationAwareLogger ?
                new LocationAwareSlf4JLogger((org.slf4j.spi.LocationAwareLogger) logger) :
                new Slf4jLogger(logger);
    }
}
