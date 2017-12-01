package ch.voulgarakis.icsc2018.recruitment.events;

/**
 * The TodoCreatedEvent can be an ordinary POJO
 */
public class TodoEvent {
    private boolean now;

    public TodoEvent(boolean now) {
        this.now = now;
    }

    public boolean isNow() {
        return now;
    }
}