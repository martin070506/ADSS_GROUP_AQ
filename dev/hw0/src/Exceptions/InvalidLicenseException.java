package Exceptions;

public class InvalidLicenseException extends DomainException {
    public InvalidLicenseException(String driver) {
        super("Driver " + driver + " does not have the required license.");
    }
}

