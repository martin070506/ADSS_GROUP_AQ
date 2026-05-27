package Domain;

import java.util.Map;

public class Request {

    private final Location location;
    private final ProductFile productFile;

    public Request(Location storeLocation, int i, Map<Product, Integer> neededItems) {
        this.location = storeLocation;
        this.productFile = new ProductFile(neededItems, i);
    }

    public void handleShipment(Truck truck) {

        Map<Product, Integer> requestedProducts = productFile.getProducts();
        truck.removeProducts(requestedProducts);
    }

    public String getContactName() {
        return location.contactName();
    }

    public Map<Product, Integer> getProducts() {
        return productFile.getProducts();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Request: " + location.address() +
                " | Contact: " + location.contactName() +
                " | File #" + productFile.getFileNumber() + "\n");

        result.append("Products to deliver:\n");

        if (productFile.getProducts() == null || productFile.getProducts().isEmpty()) {
            result.append("  (No products assigned)");
        } else {
            for (Map.Entry<Product, Integer> entry : productFile.getProducts().entrySet()) {
                result.append("  ").append(entry.getKey().name()).append(": ").append(entry.getValue()).append(" units\n");
            }
        }
        return result.toString();
    }

    public Location getLocation() {
        return location;
    }

    public void addProducts(Map<Product, Integer> neededItems) {
        productFile.addProducts(neededItems);
    }

    public void removeProducts(Map<Product, Integer> productsToRemove) {
        productFile.removeProducts(productsToRemove);
    }

    public void removeProduct(Product product, int quantityToRemove) {
        productFile.removeProduct(product, quantityToRemove);
    }

    public void addProduct(Product product, int quantity){
        productFile.addProduct(product, quantity);
    }
}
