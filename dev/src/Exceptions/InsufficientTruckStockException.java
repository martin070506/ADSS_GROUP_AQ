package Exceptions;

public class InsufficientTruckStockException extends DomainException {
    public InsufficientTruckStockException(String productName, int requested, int available) {
        super("Truck has insufficient stock for product: " + productName +
                "! Requested: " + requested + ", Available: " + available);
    }
}