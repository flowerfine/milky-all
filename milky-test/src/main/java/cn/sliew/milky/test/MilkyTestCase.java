package cn.sliew.milky.test;

import cn.sliew.milky.test.extension.random.RandomizedTestCase;
import cn.sliew.milky.test.extension.time.TimingExtension;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@ExtendWith(TimingExtension.class)
public class MilkyTestCase extends RandomizedTestCase {

    private static final Collection<String> nettyLoggedLeaks = new ArrayList<>();

    static {
        String leakLoggerName = "io.netty.util.ResourceLeakDetector";
        Logger leakLogger = LogManager.getLogger(leakLoggerName);
        Appender leakAppender = new AbstractAppender(leakLoggerName, null,
                PatternLayout.newBuilder().withPattern("%m").build()) {
            @Override
            public void append(LogEvent event) {
                String message = event.getMessage().getFormattedMessage();
                if (Level.ERROR.equals(event.getLevel()) && message.contains("LEAK:")) {
                    synchronized (nettyLoggedLeaks) {
                        nettyLoggedLeaks.add(message);
                    }
                }
            }
        };

        leakAppender.start();
        addAppender(leakLogger, leakAppender);

        // shutdown hook so that when the test JVM exits, logging is shutdown too
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            leakAppender.stop();
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configurator.shutdown(context);
        }));
    }

    private static void addAppender(final Logger logger, final Appender appender) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        config.addAppender(appender);
        LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());
        if (!logger.getName().equals(loggerConfig.getName())) {
            loggerConfig = new LoggerConfig(logger.getName(), logger.getLevel(), true);
            config.addLogger(logger.getName(), loggerConfig);
        }
        loggerConfig.addAppender(appender, null, null);
        ctx.updateLoggers();
    }

    @AfterEach
    public final void after() {
        checkStaticState();
    }

    // separate method so that this can be checked again after suite scoped cluster is shut down
    protected static void checkStaticState() {
        // ensure no one changed the status logger level on us
//        assertThat(StatusLogger.getLogger().getLevel(), equalTo(Level.WARN));
        synchronized (nettyLoggedLeaks) {
            try {
                assertThat(nettyLoggedLeaks, empty());
            } finally {
                nettyLoggedLeaks.clear();
            }
        }
    }

}
