package Exceptions;

public class NoDestinationForEmergencyDropOffException extends RuntimeException {
    public NoDestinationForEmergencyDropOffException() {
        super("No destinations left");
    }
}
