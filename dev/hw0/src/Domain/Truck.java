package Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Truck(int truckNumber, String model, int truckWeight,
                    int maxWeight, int minLicense, Map<String, ProductPair> loadedProducts) {

    public Truck(int truckNumber, String model, int truckWeight, int maxWeight, int minLicense) {
        this(truckNumber, model, truckWeight, maxWeight, minLicense, new HashMap<>());
    }

    public void addProducts(List<ProductPair> pairs) {

        if (pairs == null)
            throw new IllegalArgumentException("Null pairs are not allowed");

        int productsWeight = 0;
        for (ProductPair pair : pairs) {
            if (pair == null)
                throw new IllegalArgumentException("Null pairs are not allowed");

            productsWeight += pair.product.weight() * pair.getAmount();
        }

        if (maxWeight < truckWeight + loadedProductsWeight() + productsWeight)
            throw new IllegalArgumentException("Truck Overload");

        for (ProductPair pair : pairs) {
            String name = pair.product.name();
            if (loadedProducts.containsKey(name)) {
                int currentAmount = loadedProducts.get(name).getAmount();
                loadedProducts.get(name).setAmount(currentAmount + pair.getAmount());
            }
            else
                loadedProducts.put(name, pair);
        }
    }

    public void removeProducts(List<ProductPair> pairs) {

        if (pairs == null)
            throw new IllegalArgumentException("Null pairs are not allowed");

        for (ProductPair pair : pairs) {
            if (pair == null)
                throw new IllegalArgumentException("Null product pair");
            if (!loadedProducts.containsKey(pair.product.name()))
                throw new IllegalArgumentException("Product not found");
            if (loadedProducts.get(pair.product.name()).getAmount() < pair.getAmount())
                throw new IllegalArgumentException("Product amount too low");
        }

        for (ProductPair pair : pairs) {
            String name = pair.product.name();
            int currentAmount = loadedProducts.get(name).getAmount();
            loadedProducts.get(name).setAmount(currentAmount - pair.getAmount());
        }
    }

    private int loadedProductsWeight() {

        int weight = 0;
        for (ProductPair pair : loadedProducts.values())
            weight += pair.product.weight() * pair.getAmount();

        return weight;
    }
}
