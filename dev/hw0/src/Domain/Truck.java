package Domain;

import Exceptions.InsufficientTruckStockException;

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
            throw new NullPointerException("Null pairs are not allowed");

        for (ProductPair pair : pairs)
            if (pair == null)
                throw new NullPointerException("Null pairs are not allowed");

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
            throw new NullPointerException("Null pairs are not allowed");

        for (ProductPair pair : pairs) {
            if (pair == null)
                throw new NullPointerException("Null product pair");
            String name = pair.product.name();
            if (!loadedProducts.containsKey(name))
                throw new InsufficientTruckStockException(name, pair.getAmount(), 0);
            if (loadedProducts.get(name).getAmount() < pair.getAmount())
                throw new InsufficientTruckStockException(pair.product.name(), pair.getAmount(),
                        loadedProducts.get(name).getAmount());
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
