package cn.sliew.milky.event.reactor;

import cn.sliew.milky.event.base.EchoEventListener;
import cn.sliew.milky.event.base.LogEvent;
import cn.sliew.milky.event.base.LogEventListener;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class ReactorEventBusTest extends MilkyTestCase {

    private ReactorEventBus<LogEvent> eventBus;
    private EchoEventListener echoEventListener;
    private LogEventListener logEventListener;

    @BeforeEach
    private void beforeEach() {
        this.eventBus = new ReactorEventBus();
        this.echoEventListener = new EchoEventListener();
        this.logEventListener = new LogEventListener();
    }

    @Test
    public void testFire() {
        eventBus.register(logEventListener);
        eventBus.fire(new LogEvent(this));
    }

    @Test
    public void testFireWithWrongEventType() {
        eventBus.register(echoEventListener);
        try {
            eventBus.fire(new LogEvent(this));
            fail();
        } catch (Exception e) {

        }
    }
}
