package Exceptions;

public class OverweightException extends DomainException {
    public OverweightException(int current, int max) {
        super("Truck is overweight! Current: " + current + ", Max: " + max);
    }
}

