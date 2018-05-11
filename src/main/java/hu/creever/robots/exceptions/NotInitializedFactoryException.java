package hu.creever.robots.exceptions;

public class NotInitializedFactoryException extends RuntimeException {

    public NotInitializedFactoryException() {
        super();
    }

    NotInitializedFactoryException(Throwable cause) {
        super(cause);
    }
}
