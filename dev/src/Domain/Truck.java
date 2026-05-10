package Domain;

import Exceptions.InsufficientTruckStockException;
import Exceptions.OverweightException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Truck {
    private int currentWeight;
    private int startWeight;
    private int maxWeight;
    private int truckNumber;
    private String model;
    private int minLicense;
    private Map<String,ProductPair> loadedProducts;

    public Truck(int truckNumber, String model, int truckWeight, int maxWeight, int minLicense) {
        this.currentWeight = truckWeight;
        this.startWeight = truckWeight;
        this.maxWeight = maxWeight;
        this.model = model;
        this.truckNumber = truckNumber;
        this.minLicense = minLicense;
        loadedProducts = new HashMap<>();
    }


    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }
    public Map<String,ProductPair> getProductPairs() {
        return loadedProducts;
    }
    public void emptyTruck(){
        this.currentWeight = startWeight;
        loadedProducts.clear();
    }

    public int getTruckNumber() {
        return truckNumber;
    }

    public void transferHoldingsToOtherTruck(Truck replacement){
        replacement.setLoadedProducts(loadedProducts);
        this.loadedProducts=new HashMap<>();
    }

    public void setLoadedProducts(Map<String,ProductPair> loadedProducts) {
        this.loadedProducts = loadedProducts;
    }
    public int getMinLicense() {
        return minLicense;
    }
    public String getModel() {
        return model;
    }
    public void setCurrentWeight(int currentWeight) {
         if (currentWeight < 0)
            throw new IllegalArgumentException("Truck Weight can't be Negative");

        this.currentWeight = currentWeight;
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
                currentWeight+=pair.product.weight()*pair.getAmount();
            }
            else {
                loadedProducts.put(name, new ProductPair(pair));
                currentWeight += pair.product.weight() * pair.getAmount();
            }
        }
        if (currentWeight > maxWeight)
            throw new OverweightException(maxWeight, currentWeight, pairs);
    }
    public void removeProducts(List<ProductPair> pairs) {
        removeProducts(pairs, null);
    }

    public void removeProducts(List<ProductPair> pairs, List<ProductPair> addedProducts) {

        if (addedProducts != null)
            for (ProductPair pair : addedProducts)
                loadedProducts.get(pair.product.name()).reduceAmount(pair.getAmount());

        if (pairs == null)
            throw new NullPointerException("Null pairs are not allowed");

        try {
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
                currentWeight -= pair.product.weight() * pair.getAmount();
            }

            if (addedProducts != null)
                for (ProductPair pair : addedProducts)
                    loadedProducts.get(pair.product.name()).reduceAmount(-pair.getAmount());

        } catch (Exception e) {
            if (addedProducts != null)
                for (ProductPair pair : addedProducts)
                    loadedProducts.get(pair.product.name()).reduceAmount(-pair.getAmount());

            throw e;
        }
    }
}
