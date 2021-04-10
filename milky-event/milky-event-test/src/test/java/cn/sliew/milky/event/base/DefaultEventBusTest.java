package cn.sliew.milky.event.base;

import cn.sliew.milky.event.EventBus;
import cn.sliew.milky.event.reflection.DefaultEventBus;
import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultEventBusTest extends MilkyTestCase {

    private EventBus eventBus;
    private EchoEventListener echoEventListener;

    @BeforeEach
    private void beforeEach() {
        this.eventBus = new DefaultEventBus();
        this.echoEventListener = new EchoEventListener();
    }

    @Test
    public void testGetExecutor() {
        assertNotNull(eventBus.getExecutor());
    }


}
