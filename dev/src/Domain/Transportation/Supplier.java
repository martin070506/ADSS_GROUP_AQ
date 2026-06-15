package Domain.Transportation;

import Exceptions.InsufficientSupplierStockException;

import java.util.*;

public class Supplier {

    private final Location location;
    private final Map<Integer,Integer> productsAvailable; 
    
    public Supplier(Location location, Map<Integer, Integer> productsAvailable) {
        this.location = location;
        this.productsAvailable = productsAvailable;
    }

    public String getName() {
        return location.contactName();
    }

    public void handleShipment(Map<Integer, Integer> supplierAllocationIds) {
        checkAvailability(supplierAllocationIds);
        dispatchProducts(supplierAllocationIds);
    }

    private void checkAvailability(Map<Integer, Integer> supplierAllocationIds) {
        for (Map.Entry<Integer, Integer> entry : supplierAllocationIds.entrySet()) {
            int productId = entry.getKey();
            int requiredAmount = entry.getValue();
            int availableAmount = productsAvailable.getOrDefault(productId, 0);

            if (availableAmount < requiredAmount)
                throw new InsufficientSupplierStockException(productId, requiredAmount, availableAmount);
        }
    }

    private void dispatchProducts(Map<Integer, Integer> supplierAllocationIds) {
        for (Map.Entry<Integer, Integer> entry : supplierAllocationIds.entrySet()) {
            int productId = entry.getKey();
            int amount = entry.getValue();
            productsAvailable.put(productId, productsAvailable.get(productId) - amount);
        }
    }

    public int getLocationId() {
        return location.id();
    }

    public List<Integer> getProductIds() {
        return new ArrayList<>(productsAvailable.keySet());
    }

    public void addStock(int productId, int amount) {
        productsAvailable.put(productId, productsAvailable.getOrDefault(productId, 0) + amount);
    }

    public int getProductStock(int productId) {
        for (Map.Entry<Integer, Integer> entry : productsAvailable.entrySet())
            if (entry.getKey() == productId)
                return entry.getValue();

        return 0;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}