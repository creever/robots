package hu.creever.robots.exceptions;

public class MissingFactoryConfigPropertyException extends RuntimeException {

    public MissingFactoryConfigPropertyException(String fieldName) {
        super(fieldName);
    }

    MissingFactoryConfigPropertyException(String fieldName, Throwable cause) {
        super(fieldName, cause);
    }
}
