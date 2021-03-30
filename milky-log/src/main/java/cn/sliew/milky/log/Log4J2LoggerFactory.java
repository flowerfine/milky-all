package cn.sliew.milky.log;

import org.apache.logging.log4j.LogManager;

public final class Log4J2LoggerFactory extends LoggerFactory {

    public static final LoggerFactory INSTANCE = new Log4J2LoggerFactory();

    @Override
    public Logger newInstance(String name) {
        return new Log4J2Logger(LogManager.getLogger(name));
    }
}