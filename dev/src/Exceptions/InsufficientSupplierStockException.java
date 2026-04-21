package Exceptions;

public class InsufficientSupplierStockException extends DomainException {
    public InsufficientSupplierStockException(String productName, int requested, int available) {
        super("Supplier has insufficient stock for product: " + productName +
                "! Requested: " + requested + ", Available: " + available);
    }
}