package Domain;

import Exceptions.DomainException;
import Exceptions.InsufficientSupplierStockException;

import java.util.*;

public class Supplier {

    private Location supplierLocation;
    private Map<Product,Integer> productsAvailable;
    public Supplier(Location supplierLocation) {
        this.supplierLocation = supplierLocation;
        productsAvailable = new HashMap<>();
    }
    public Supplier(Location supplierLocation, Map<Product, Integer> productsAvailable) {
        this.supplierLocation = supplierLocation;
        this.productsAvailable = productsAvailable;
    }

    public String getName() {
        return supplierLocation.contactName();
    }

    public void handleShipment(Map<Product, Integer> supplierAllocations, Truck truck) {
        checkAvailability(supplierAllocations);
        truck.addProducts(supplierAllocations);
        dispatchProducts(supplierAllocations);
    }

    private void checkAvailability(Map<Product, Integer> supplierAllocations) {
        for (Map.Entry<Product, Integer> entry : supplierAllocations.entrySet()) {
            Product product = entry.getKey();
            int requiredAmount = entry.getValue();
            int availableAmount = productsAvailable.getOrDefault(product, 0);

            if (availableAmount < requiredAmount) {
                throw new InsufficientSupplierStockException(product.name(), requiredAmount, availableAmount);
            }
        }
    }

    private void dispatchProducts(Map<Product, Integer> supplierAllocations) {
        for (Map.Entry<Product, Integer> entry : supplierAllocations.entrySet()) {
            Product product = entry.getKey();
            int amountToTake = entry.getValue();
            productsAvailable.put(product, productsAvailable.get(product) - amountToTake);
        }
    }

    public Product getProductByIndex(int index) {
        List<Product> products = new ArrayList<>(productsAvailable.keySet());
        return products.get(index);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(supplierLocation.toString());
        result.append(".    Available Products:\n");

        if (productsAvailable.isEmpty()) {
            result.append("  (Empty Inventory)");
        } else {
            int i = 1;
            for (Map.Entry<Product, Integer> entry : productsAvailable.entrySet()) {
                result.append("  ").append(i).append(". Product: ").append(entry.getKey().name())
                        .append(" - Amount: ").append(entry.getValue()).append("\n");
                i++;
            }
        }

        return result.toString();
    }

    public Location getSupplierLocation() {
        return supplierLocation;
    }

    public Map<Product, Integer> getProductsAvailable() {
        return productsAvailable;
    }

    public void addStock(Product product, int amount) {
        productsAvailable.put(product, productsAvailable.getOrDefault(product, 0) + amount);
    }

    public static Map<Supplier, Map<String, Integer>> mapIndexesToSuppliers(
            List<Supplier> suppliers,
            Map<Integer, Map<String, Integer>> rawAllocations) {

        Map<Supplier, Map<String, Integer>> supplierMap = new HashMap<>();

        for (Map.Entry<Integer, Map<String, Integer>> entry : rawAllocations.entrySet()) {
            int supplierIndex = entry.getKey();
            Map<String , Integer> productAllocations = entry.getValue();

            if (supplierIndex < 0 || supplierIndex >= suppliers.size()) {
                throw new DomainException("Supplier index out of bounds: " + supplierIndex);
            }

            Supplier supplier = suppliers.get(supplierIndex);
            supplierMap.put(supplier, productAllocations);
        }

        return supplierMap;
    }
}