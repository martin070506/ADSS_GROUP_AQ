package Domain;

import Exceptions.DomainException;
import Exceptions.ProductNotFoundOnTruckException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param name   The product name.
 * @param weight The weight in Kilograms (Kg). Must be a positive value.
 * */

public record Product(int id,String name, int weight) {

    @Override
    public String toString() {
        return "ID: "+  id + " ==> " +name + " (" + weight + " Kg)";
    }

    static Map<Product, Integer> reduceProducts(Map<Product, Integer> currentProducts, Map<Product, Integer> productsToRemove) {
        for (Map.Entry<Product, Integer> entry : productsToRemove.entrySet()) {
            Product product = entry.getKey();
            int requestedAmount = entry.getValue();

            if (!currentProducts.containsKey(product)) {
                throw new ProductNotFoundOnTruckException(product.name());
            }

            int availableAmount = currentProducts.get(product);

            if (availableAmount < requestedAmount) {
                throw new ProductNotFoundOnTruckException(product.name(), requestedAmount, availableAmount);
            }

            else if (availableAmount == requestedAmount) {
                currentProducts.remove(product);
            } else {
                currentProducts.put(product, availableAmount - requestedAmount);
            }
        }
        return currentProducts;
    }

    static Map<Product, Integer> combineProducts(Map<Product, Integer> baseMap, Map<Product, Integer> itemsToAdd) {
        Map<Product, Integer> combined = new HashMap<>(baseMap);
        for (Map.Entry<Product, Integer> entry : itemsToAdd.entrySet()) {
            combined.put(entry.getKey(), combined.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
        return combined;
    }
}
