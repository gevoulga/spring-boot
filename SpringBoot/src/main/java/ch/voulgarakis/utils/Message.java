package ch.voulgarakis.utils;

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

    public String getFrom() {
        return from;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getPayload() {
        return payload;
    }
}