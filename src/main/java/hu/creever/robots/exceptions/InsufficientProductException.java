package hu.creever.robots.exceptions;

public class InsufficientProductException extends RuntimeException {

    public InsufficientProductException() {
        super();
    }

    InsufficientProductException(Throwable cause) {
        super(cause);
    }
}
