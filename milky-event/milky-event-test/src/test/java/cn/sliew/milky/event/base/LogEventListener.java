package cn.sliew.milky.event.base;

import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;

public class LogEventListener extends AbstractEventListener<LogEvent> {

    private static final Logger log = LoggerFactory.getLogger(LogEventListener.class);

    @Override
    protected void handleEvent(LogEvent event) {
        log.info("{} : {}", this.getClass().getSimpleName(), event);
    }
}
