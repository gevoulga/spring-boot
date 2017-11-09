package ch.voulgarakis.recruitment.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TodoEventProducer {
    @Autowired
    private ApplicationEventPublisher publisher;

    // @Autowired
    // public TodoEventProducer(ApplicationEventPublisher publisher) {
    // }

    public void createTodo(boolean now) {
        publisher.publishEvent(new TodoEvent(now));
    }
}
