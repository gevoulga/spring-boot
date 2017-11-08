package ch.voulgarakis.recruitment.utils;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 2039148198687076003L;
    private String message;

    protected ChatMessage() {
        // Default constructor
    }

    public ChatMessage(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
