package cn.sliew.milky.event.base;

import cn.sliew.milky.event.EventBus;
import cn.sliew.milky.event.reflection.DefaultEventBus;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultEventBusTest extends MilkyTestCase {

    private EventBus eventBus;
    private EchoEventListener echoEventListener;
    private LogEventListener logEventListener;

    @BeforeEach
    private void beforeEach() {
        this.eventBus = new DefaultEventBus();
        this.echoEventListener = new EchoEventListener();
        this.logEventListener = new LogEventListener();
    }

    @Test
    public void testGetExecutor() {
        assertNotNull(eventBus.getExecutor());
    }

    @Test
    public void testRegisterEventListener() {
        eventBus.register(echoEventListener);
        assertEquals(0, echoEventListener.getEventOccurs());
        eventBus.fire(new EchoEvent(this));
        try {
            TimeUnit.MILLISECONDS.sleep(10L);
        } catch (InterruptedException e) {

        }
        assertEquals(1, echoEventListener.getEventOccurs());
    }

    @Test
    public void testRegisterEventListenerWithEventClass() {
        eventBus.register(EchoEvent.class, echoEventListener);
        assertEquals(0, echoEventListener.getEventOccurs());
        eventBus.fire(new EchoEvent(this));
        try {
            TimeUnit.MILLISECONDS.sleep(10L);
        } catch (InterruptedException e) {

        }
        assertEquals(1, echoEventListener.getEventOccurs());
    }

    @Test
    public void testRegisterTwice() {
        eventBus.register(EchoEvent.class, echoEventListener);
        eventBus.register(LogEvent.class, logEventListener);
        try {
            eventBus.register(echoEventListener);
            fail();
        } catch (Exception e) {

        }
    }

    @Test
    public void testFireWithEventListener() {
        eventBus.register(EchoEvent.class, echoEventListener);
        eventBus.register(LogEvent.class, logEventListener);
        assertEquals(0, echoEventListener.getEventOccurs());
        eventBus.fire(new EchoEvent(this));
        try {
            TimeUnit.MILLISECONDS.sleep(10L);
        } catch (InterruptedException e) {

        }
        assertEquals(1, echoEventListener.getEventOccurs());
        assertEquals(0, logEventListener.getEventOccurs());
    }

    @Test
    public void testFireWithMultiEventListeners() {
        EchoEventListener2 echoEventListener2 = new EchoEventListener2();
        eventBus.register(echoEventListener);
        eventBus.register(echoEventListener2);
        eventBus.register(logEventListener);
        assertEquals(0, echoEventListener.getEventOccurs());
        assertEquals(0, echoEventListener2.getEventOccurs());
        eventBus.fire(new EchoEvent(this));
        try {
            TimeUnit.MILLISECONDS.sleep(10L);
        } catch (InterruptedException e) {

        }
        assertEquals(1, echoEventListener.getEventOccurs());
        assertEquals(1, echoEventListener2.getEventOccurs());
        assertEquals(0, logEventListener.getEventOccurs());
    }






}
