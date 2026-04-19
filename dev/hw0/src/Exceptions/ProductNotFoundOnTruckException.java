package Exceptions;

public class ProductNotFoundOnTruckException extends DomainException {

    public ProductNotFoundOnTruckException(String productName) {
        super("Error: Product '" + productName + "' not found on truck.");
    }

    public ProductNotFoundOnTruckException(String productName, int requestedAmount, int availableAmount) {
        super("Error: Not enough quantity for '" + productName + "' on the truck. Requested: " + requestedAmount + " | Available: " + availableAmount);
    }
}