package cn.sliew.milky.common.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

import java.security.AccessController;
import java.security.PrivilegedAction;

import static cn.sliew.milky.common.log.AbstractLogger.EXCEPTION_MESSAGE;


class Log4J2Logger extends ExtendedLoggerWrapper implements cn.sliew.milky.common.log.Logger {

    private static final long serialVersionUID = 5485418394879791397L;
    private static final boolean VARARGS_ONLY;

    static {
        // Older Log4J2 versions have only log methods that takes the format + varargs. So we should not use
        // Log4J2 if the version is too old.
        // See https://github.com/netty/netty/issues/8217
        VARARGS_ONLY = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                try {
                    Logger.class.getMethod("debug", String.class, Object.class);
                    return false;
                } catch (NoSuchMethodException ignore) {
                    // Log4J2 version too old.
                    return true;
                } catch (SecurityException ignore) {
                    // We could not detect the version so we will use Log4J2 if its on the classpath.
                    return false;
                }
            }
        });
    }

    Log4J2Logger(Logger logger) {
        super((ExtendedLogger) logger, logger.getName(), logger.getMessageFactory());
        if (VARARGS_ONLY) {
            throw new UnsupportedOperationException("Log4J2 version mismatch");
        }
    }

    @Override
    public String name() {
        return getName();
    }

    @Override
    public void trace(Throwable t) {
        log(Level.TRACE, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void debug(Throwable t) {
        log(Level.DEBUG, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void info(Throwable t) {
        log(Level.INFO, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void warn(Throwable t) {
        log(Level.WARN, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void error(Throwable t) {
        log(Level.ERROR, EXCEPTION_MESSAGE, t);
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return isEnabled(toLevel(level));
    }

    @Override
    public void log(LogLevel level, String msg) {
        log(toLevel(level), msg);
    }

    @Override
    public void log(LogLevel level, String format, Object arg) {
        log(toLevel(level), format, arg);
    }

    @Override
    public void log(LogLevel level, String format, Object argA, Object argB) {
        log(toLevel(level), format, argA, argB);
    }

    @Override
    public void log(LogLevel level, String format, Object... arguments) {
        log(toLevel(level), format, arguments);
    }

    @Override
    public void log(LogLevel level, String msg, Throwable t) {
        log(toLevel(level), msg, t);
    }

    @Override
    public void log(LogLevel level, Throwable t) {
        log(toLevel(level), EXCEPTION_MESSAGE, t);
    }

    private static Level toLevel(LogLevel level) {
        switch (level) {
            case INFO:
                return Level.INFO;
            case DEBUG:
                return Level.DEBUG;
            case WARN:
                return Level.WARN;
            case ERROR:
                return Level.ERROR;
            case TRACE:
                return Level.TRACE;
            default:
                throw new Error();
        }
    }
}
