package hu.creever.robots.exceptions;

public class MissingProductException extends RuntimeException {

    public MissingProductException(String productName, String robotSerialNumber, String phase) {
        super(productName);
        this.robotSerialNumber = robotSerialNumber;
        this.phase = phase;
    }

    MissingProductException(String productName, String robotSerialNumber, String phase, Throwable cause) {
        super(productName, cause);
        this.robotSerialNumber = robotSerialNumber;
        this.phase = phase;
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return the serial number of the robot
     */
    public String getRobotSerialNumber() {
        return robotSerialNumber;
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return current phase where the exception occured
     */
    public String getPhase() {
        return phase;
    }

    /**
     * The class name of the resource bundle requested by the user.
     * @serial
     */
    private String robotSerialNumber;

    /**
     * The name of the specific resource requested by the user.
     * @serial
     */
    private String phase;
}
