package ch.voulgarakis.icsc2018.recruitment.events;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TodoEventListener {
    private Logger logger = LoggerFactory.getLogger(TodoEventListener.class);
    private List<TodoEvent> events = new ArrayList<>();

    @EventListener(condition = "#todoEvent.now == true")
    public void doItNow(TodoEvent todoEvent) {
        events.add(todoEvent);
        logger.info("I should be doing this now...");
    }

    @EventListener(condition = "#todoEvent.now == false")
    public void doItLater(TodoEvent todoEvent) {
        events.add(todoEvent);
        logger.info("I will do this later!..");
    }

    public List<TodoEvent> getEvents() {
        return events;
    }
}
