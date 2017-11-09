package ch.voulgarakis.recruitment.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TodoEventListener {
    Logger logger = LoggerFactory.getLogger(TodoEventListener.class);

    @EventListener(condition = "#todoEvent.now == true")
    void doItNow(TodoEvent todoEvent) {
        logger.info("I should be doing this now...");
    }

    @EventListener(condition = "#todoEvent.now == false")
    void doItLater(TodoEvent todoEvent) {
        logger.info("I will do this later!..");
    }
}
