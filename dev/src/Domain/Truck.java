package Domain;

import Exceptions.OverweightException;

import java.util.HashMap;
import java.util.Map;

public class Truck {
    private final int startWeight;
    private final int maxWeight;
    private final int truckNumber;
    private final String model;
    private final int minLicense;
    private boolean isAvailable;
    private  int id;
    private Map<Product, Integer> loadedProducts;


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
        this.id=id;
    }

    public int getId() {
        return id;
    }



    public int getCurrentWeight() {
        int currentWeight = startWeight;
        for (Map.Entry<Product, Integer> entry : loadedProducts.entrySet())
            currentWeight += entry.getKey().weight() * entry.getValue();
        return currentWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }
    public Map<Product, Integer> getLoadedProducts() {
        return loadedProducts;
    }
    public void emptyTruck(){
        removeProducts(new HashMap<>(loadedProducts));
    }

    public int getTruckNumber() {
        return truckNumber;
    }

    public void transferHoldingsToOtherTruck(Truck replacement){
        replacement.emptyTruck();
        replacement.addProducts(loadedProducts);
        emptyTruck();
    }

    public int getMinLicense() {
        return minLicense;
    }
    public String getModel() {
        return model;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void addProducts(Map<Product, Integer> newProducts) {
        this.loadedProducts = Product.combineProducts(this.loadedProducts, newProducts);
        if (getCurrentWeight() > maxWeight)
            throw new OverweightException(getCurrentWeight(), maxWeight, newProducts);
    }

    public void removeProducts(Map<Product, Integer> productsToRemove) {
        loadedProducts = Product.reduceProducts(new HashMap<>(loadedProducts), productsToRemove);
    }

    @Override
    public String toString() {
        return "ID: "+id +"Truck #" + truckNumber + " [" + model + "] | Max Weight: " +
                maxWeight + "kg | Min License: " + minLicense;
    }
}
