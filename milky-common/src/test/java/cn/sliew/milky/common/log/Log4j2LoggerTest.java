package cn.sliew.milky.common.log;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

public class Log4j2LoggerTest extends MilkyTestCase {

    @Test
    public void test() {
//        LoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.error("hhh");
    }
}
