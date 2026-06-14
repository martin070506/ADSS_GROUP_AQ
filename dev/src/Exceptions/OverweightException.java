package Exceptions;

import Domain.Transportation.Product;

import java.util.Map;

public class OverweightException extends DomainException {

    private final Map<Product, Integer> addedProducts;
    public OverweightException(int current, int max, Map<Product, Integer> addedProducts) {
        super("Truck is overweight! Current: " + current + ", Max: " + max);
        this.addedProducts = addedProducts;
    }

    public Map<Product, Integer> getAddedProducts() {
        return addedProducts;
    }

}

