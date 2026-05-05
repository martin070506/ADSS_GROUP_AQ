package Domain;

import Exceptions.InsufficientSupplierStockException;

import java.util.List;

public record Supplier(Location supplierLocation, List<ProductPair> productsAvailable) {

    public String getName() {
        return supplierLocation.contactName();
    }

    public void handleShipment(List<ProductPair> supplierAllocations, Truck truck) {

        checkAvailability(supplierAllocations);
        truck.addProducts(supplierAllocations);
        dispatchProducts(supplierAllocations);
    }

    private void checkAvailability(List<ProductPair> supplierAllocations) {

        for (ProductPair pair : supplierAllocations) {
            boolean productFound = false;

            for (ProductPair pair2 : productsAvailable)
                if (pair2.product == pair.product) {
                    if (pair2.getAmount() < pair.getAmount())
                        throw new InsufficientSupplierStockException(pair.product.name(), pair.getAmount(),
                                pair2.getAmount());

                    productFound = true;
                }

            if (!productFound)
                throw new InsufficientSupplierStockException(pair.product.name(), pair.getAmount(), 0);
        }
    }


    private void dispatchProducts(List<ProductPair> supplierAllocations) {

        for (ProductPair pair : supplierAllocations)
            for (ProductPair pair2 : productsAvailable)
                if (pair2.product == pair.product) {
                    /// WE CHECKED FOR AVAILABILITY SO NO NEED TO  CHECK FOR NEGATIVE AMOUNT HERE
                    pair2.setAmount(pair2.getAmount() - pair.getAmount());
                    break;
                }
    }

    public Product getProductByIndex(int index) {
        return productsAvailable.get(index).product;
    }

    @Override
    public String toString() {
        String result = supplierLocation.toString();

        result += ".    Available Products:\n";

        if (productsAvailable.isEmpty())
            result += "  (Empty Inventory)";
        else
            for (int i = 0; i < productsAvailable.size(); i++)
                result += "  " + (i + 1) + ". " + productsAvailable.get(i).toString() + "\n";

        return result;
    }

    public Location getSupplierLocation() {
        return supplierLocation;
    }

    public List<ProductPair> getProductsAvailable() {
        return productsAvailable;
    }

    public void addStock(Product product, int amount) {

        for(ProductPair pair : productsAvailable){
            if(pair.product == product){
                pair.setAmount(pair.getAmount() + amount);
                return;
            }
        }

        productsAvailable.add(new ProductPair(product, amount));
    }
}
