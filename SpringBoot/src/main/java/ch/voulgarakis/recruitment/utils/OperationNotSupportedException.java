package ch.voulgarakis.recruitment.utils;

public class OperationNotSupportedException extends RuntimeException {
    private static final long serialVersionUID = -1048428957550661744L;

    public OperationNotSupportedException(String message) {
        super(message);
    }

    public OperationNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
