package Exceptions;

import Domain.ProductPair;

import java.util.List;

public class OverweightException extends DomainException {

    private final List<ProductPair> addedProducts;
    public OverweightException(int current, int max, List<ProductPair> addedProducts) {
        super("Truck is overweight! Current: " + current + ", Max: " + max);
        this.addedProducts = addedProducts;
    }

    public List<ProductPair> getAddedProducts() {
        return addedProducts;
    }
}

