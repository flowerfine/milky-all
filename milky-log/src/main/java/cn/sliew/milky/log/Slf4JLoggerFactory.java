package cn.sliew.milky.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Logger factory which creates a <a href="http://www.slf4j.org/">SLF4J</a>
 * logger.
 */
public class Slf4JLoggerFactory extends cn.sliew.milky.common.log.LoggerFactory {

    @SuppressWarnings("deprecation")
    public static final cn.sliew.milky.common.log.LoggerFactory INSTANCE = new Slf4JLoggerFactory(true);

    Slf4JLoggerFactory(boolean failIfNOP) {
        assert failIfNOP; // Should be always called with true.
        if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
            throw new NoClassDefFoundError("NOPLoggerFactory not supported");
        }
    }

    @Override
    public cn.sliew.milky.common.log.Logger newInstance(String name) {
        return wrapLogger(LoggerFactory.getLogger(name));
    }

    // package-private for testing.
    static cn.sliew.milky.common.log.Logger wrapLogger(Logger logger) {
        return logger instanceof LocationAwareLogger ?
                new LocationAwareSlf4JLogger((LocationAwareLogger) logger) : new Slf4JLogger(logger);
    }
}
