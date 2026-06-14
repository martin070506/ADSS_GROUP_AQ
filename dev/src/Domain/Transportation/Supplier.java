package Domain.Transportation;

import Exceptions.DomainException;
import Exceptions.InsufficientSupplierStockException;

import java.util.*;

public class Supplier {

    private final Location supplierLocation;
    private final Map<Product,Integer> productsAvailable;
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

    public Product getProductById(int id) {
        List<Product> products = new ArrayList<>(productsAvailable.keySet());
        for(Product product : products) {
            if (product.id() == id) {
                return product;
            }
        }
        throw new DomainException("Product with id " + id + " not found");
    }

    @Override
    public String toString() {
        return supplierLocation.toString();
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

    public int getProductStock(int i) {
        return productsAvailable.get(getProductById(i));
    }
}