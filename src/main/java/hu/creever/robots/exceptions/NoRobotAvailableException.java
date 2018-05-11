package hu.creever.robots.exceptions;

public class NoRobotAvailableException extends RuntimeException {

    public NoRobotAvailableException() {
        super();
    }

    NoRobotAvailableException(Throwable cause) {
        super(cause);
    }
}
