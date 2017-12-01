package ch.voulgarakis.icsc2018.commons;

public class Message {
    private String from;
    private Class<?> clazz;
    private Object payload;

    public Message(String from, Class<?> clazz, Object payload) {
        super();
        this.from = from;
        this.clazz = clazz;
        this.payload = payload;
    }

    public String from() {
        return from;
    }

    public Class<?> clazz() {
        return clazz;
    }

    public Object payload() {
        return payload;
    }
}