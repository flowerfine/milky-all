package cn.sliew.milky.log;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggerFactoryTest extends MilkyTestCase {

    @Test
    void shouldUseSlf4j() {
        LoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
        Logger logger = LoggerFactory.getLogger(Object.class);
        logSomething(logger);
        assertEquals(logger.getClass().getName(), Slf4JLogger.class.getName());
    }


    @Test
    void shouldUseLog4J2() {
        LoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        Logger logger = LoggerFactory.getLogger(Object.class);
        logSomething(logger);
        assertEquals(logger.getClass().getName(), Log4J2Logger.class.getName());
    }



    @Test
    void shouldUseStdOut() {
        LoggerFactory.setDefaultFactory(StdOutLoggerFactory.INSTANCE);
        Logger logger = LoggerFactory.getLogger(Object.class);
        logSomething(logger);
        assertEquals(logger.getClass().getName(), StdOutLogger.class.getName());
    }

    @Test
    void shouldUseNoLogging() {
        LoggerFactory.setDefaultFactory(NoLoggerFactory.INSTANCE);
        Logger logger = LoggerFactory.getLogger(Object.class);
        logSomething(logger);
        assertEquals(logger.getClass().getName(), NoLogger.class.getName());
    }

    private void logSomething(Logger log) {
        log.warn("Warning message.");
        log.debug("Debug message.");
        log.error("Error message.");
        log.error("Error with Exception.", new Exception("Test exception."));
    }
}
