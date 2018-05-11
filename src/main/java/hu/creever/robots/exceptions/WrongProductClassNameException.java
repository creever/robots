package hu.creever.robots.exceptions;

public class WrongProductClassNameException extends RuntimeException {

    public WrongProductClassNameException(String fieldName) {
        super(fieldName);
    }

    WrongProductClassNameException(String fieldName, Throwable cause) {
        super(fieldName, cause);
    }
}
