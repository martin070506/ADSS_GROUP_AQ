package Exceptions;

public class InsufficientTruckStockException extends DomainException {
    int productId;
    int requested;
    int available;

    public InsufficientTruckStockException(int productId, int requested, int available) {
        super("Truck has insufficient stock.");
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }
}