package cn.sliew.milky.log;

/**
 * Creates an {@link Logger} or changes the default factory
 * implementation.  This factory allows you to choose what logging framework
 * Netty should use.  The default factory is {@link Slf4jLoggerFactory}.  If SLF4J
 * is not available, {@link Log4J2LoggerFactory} is used.  You can change it to your preferred
 * logging framework before other Netty classes are loaded:
 * <pre>
 * {@link LoggerFactory}.setDefaultFactory({@link Log4J2LoggerFactory}.INSTANCE);
 * </pre>
 * Please note that the new default factory is effective only for the classes
 * which were loaded after the default factory is changed.  Therefore,
 * {@link #setDefaultFactory(LoggerFactory)} should be called as early
 * as possible and shouldn't be called more than once.
 */
public abstract class LoggerFactory {

    private static volatile LoggerFactory defaultFactory;

    @SuppressWarnings("UnusedCatchParameter")
    private static LoggerFactory newDefaultFactory(String name) {
        LoggerFactory f;
        try {
            f = new Slf4jLoggerFactory(true);
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
        } catch (Throwable ignore1) {
            try {
                f = Log4J2LoggerFactory.INSTANCE;
                f.newInstance(name).debug("Using Log4J2 as the default logging framework");
            } catch (Throwable ignore2) {
                f = NoLoggerFactory.INSTANCE;
                f.newInstance(name).debug("Using NoLog as the default logging framework");
            }
        }
        return f;
    }

    /**
     * Returns the default factory.  The initial default factory is
     * {@link Slf4jLoggerFactory}.
     */
    public static LoggerFactory getDefaultFactory() {
        if (defaultFactory == null) {
            defaultFactory = newDefaultFactory(LoggerFactory.class.getName());
        }
        return defaultFactory;
    }

    /**
     * Changes the default factory.
     */
    public static void setDefaultFactory(LoggerFactory defaultFactory) {
        if (defaultFactory == null) {
            throw new NullPointerException("defaultFactory");
        }
        LoggerFactory.defaultFactory = defaultFactory;
    }

    /**
     * Creates a new logger instance with the name of the specified class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Creates a new logger instance with the specified name.
     */
    public static Logger getLogger(String name) {
        return getDefaultFactory().newInstance(name);
    }

    /**
     * Creates a new logger instance with the specified name.
     */
    protected abstract Logger newInstance(String name);
}
