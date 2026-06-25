package Exceptions;

public class ProductNotFoundOnTruckException extends DomainException {

    int productId;
    int requestedAmount;
    int availableAmount;

    public ProductNotFoundOnTruckException(int productId, int requestedAmount, int availableAmount) {
        super("Product not found on truck! Product ID: " + productId + ", Requested Amount: " + requestedAmount + ", Available Amount: " + availableAmount);
        this.productId = productId;
        this.requestedAmount = requestedAmount;
        this.availableAmount = availableAmount;
    }
}