package Domain.Transportation;

import Exceptions.InsufficientTruckStockException;

import java.util.HashMap;
import java.util.Map;

public class Truck {
    private final int startWeight;
    private final int maxWeight;
    private final int truckNumber;
    private final String model;
    private final int minLicense;
    private boolean isAvailable;
    private final int id;
    private final Map<Integer, Integer> loadedProducts;


    public Truck(int id,int truckNumber, String model, int startWeight, int maxWeight, int minLicense) {
        if (maxWeight < startWeight) throw new IllegalArgumentException("Max Weight must be greater than truck Weight");
        if (minLicense < 0) throw new IllegalArgumentException("Required License must be greater than 0");
        if (truckNumber < 0) throw new IllegalArgumentException("Truck Number must be greater than 0");
        this.startWeight = startWeight;
        this.maxWeight = maxWeight;
        this.model = model;
        this.truckNumber = truckNumber;
        this.minLicense = minLicense;
        isAvailable = true;
        loadedProducts = new HashMap<>();
        this.id = id;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getId() {
        return id;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public Map<Integer, Integer> getLoadedProducts() {
        return loadedProducts;
    }

    public void emptyTruck(){
        loadedProducts.clear();
    }

    public int getStartWeight() {
        return startWeight;
    }

    public void transferHoldingsToOtherTruck(Truck replacement){
        replacement.emptyTruck();
        replacement.addProducts(loadedProducts);
        emptyTruck();
    }

    public int getMinLicense() {
        return minLicense;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void addProducts(Map<Integer, Integer> newProducts) {
        for (Map.Entry<Integer, Integer> entry : newProducts.entrySet()) {
            int productId = entry.getKey();
            int amount = entry.getValue();
            loadedProducts.put(productId, loadedProducts.getOrDefault(productId, 0) + amount);
        }

        removeEmptyProducts(newProducts);
    }

    private void removeEmptyProducts(Map<Integer, Integer> productsToRemove) {
        for (Map.Entry<Integer, Integer> entry : productsToRemove.entrySet()) {
            int productId = entry.getKey();
            if (loadedProducts.get(productId) == 0)
                loadedProducts.remove(productId);
        }
    }

    public void removeProducts(Map<Integer, Integer> productsToRemove) {
        for (Map.Entry<Integer, Integer> entry : productsToRemove.entrySet()) {
            int productId = entry.getKey();
            int amount = entry.getValue();
            if (loadedProducts.getOrDefault(productId, 0) < amount)
                throw new InsufficientTruckStockException(productId, amount, loadedProducts.getOrDefault(productId, 0));
        }

        for (Map.Entry<Integer, Integer> entry : productsToRemove.entrySet()) {
            int productId = entry.getKey();
            int amount = entry.getValue();
            loadedProducts.put(productId, loadedProducts.get(productId) - amount);
        }

        removeEmptyProducts(productsToRemove);
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Truck #" + truckNumber + " [" + model + "] | Max Weight: " +
                maxWeight + "kg | Min License: " + minLicense;
    }

    public int getTruckNumber() {
        return truckNumber;
    }

    public String getModel() {
        return model;
    }
}
