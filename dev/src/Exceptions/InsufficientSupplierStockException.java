package Exceptions;

public class InsufficientSupplierStockException extends DomainException {
    int productId;
    int requested;
    int available;

    public InsufficientSupplierStockException(int productId, int requested, int available) {
        super("Supplier has insufficient stock.");
        this.productId = this.productId;
        this.requested = requested;
        this.available = available;
    }
}