package Domain;

import java.util.List;

public record Supplier(Location supplierLocation, List<ProductPair> productsAvailable) {

    public String getName() {
        return supplierLocation.contactName();
    }

    public void printInventory() {
        System.out.println("--- Inventory for Supplier: " + supplierLocation.contactName() + " ---");

        if (productsAvailable == null || productsAvailable.isEmpty()) {
            System.out.println("No products currently in stock.");
            return;
        }

        for (ProductPair pair : productsAvailable) {
            System.out.println("- " + pair.toString());
        }
    }

    public void handleShipment(List<ProductPair> supplierAllocations, Truck truck) {

        if (!checkAvailability(supplierAllocations))
            throw new IllegalArgumentException("Supplier is not available for this products");

        truck.addProducts(supplierAllocations);
        dispatchProducts(supplierAllocations);
    }

    private boolean checkAvailability(List<ProductPair> supplierAllocations) {

        for (ProductPair pair : supplierAllocations) {
            boolean productFound = false;

            for (ProductPair pair2 : productsAvailable)
                if (pair2.product == pair.product) {
                    if (pair2.getAmount() < pair.getAmount())
                        // System.out.println("Product " + pair2.product.name() + " is lower than the required " + pair.getAmount());
                        return false;

                    productFound = true;
                }

            if (!productFound)
                // System.out.println("Product " + pair.product.name() + " does not exist in the supplier stock.");
                return false;
        }

        return true;
    }

    private void dispatchProducts(List<ProductPair> supplierAllocations) {

        if (!checkAvailability(supplierAllocations))
            throw new IllegalArgumentException("Could not dispatch available products for this supplier");

        for (ProductPair pair : supplierAllocations)
            for (ProductPair pair2 : productsAvailable)
                if (pair2.product == pair.product) {
                    pair2.setAmount(pair2.getAmount() - pair.getAmount());
                    break;
                }
    }
}
