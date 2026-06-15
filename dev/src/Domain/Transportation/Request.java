package Domain.Transportation;

import java.util.Map;

public class Request {

    private final Location location;
    private final ProductFile productFile;

    public Request(Location location, int fileNumber, Map<Integer, Integer> neededItems) {
        this.location = location;
        this.productFile = new ProductFile(neededItems, fileNumber);
    }

    public void handleShipment(Map<Integer, Integer> truckProducts) {
        Map<Integer, Integer> requestedProducts = productFile.getProducts();

        for (Map.Entry<Integer, Integer> entry : requestedProducts.entrySet()) {
            int productId = entry.getKey();
            int requestedAmount = entry.getValue();
            int availableAmount = truckProducts.getOrDefault(productId, 0);

            if (availableAmount < requestedAmount)
                throw new Exceptions.ProductNotFoundOnTruckException(productId, requestedAmount, availableAmount);
        }
    }

    public String getContactName() {
        return location.contactName();
    }

    public Map<Integer, Integer> getProducts() {
        return productFile.getProducts();
    }

    @Override
    public String toString() {
        return "Request: " + location.address() +
                " | Contact: " + location.contactName() +
                " | File #" + productFile.getFileNumber() + "\n";
    }

    public Location getLocation() {
        return location;
    }

    public void addProducts(Map<Integer, Integer> neededItems) {
        productFile.addProducts(neededItems);
    }

    public void addProduct(int productId, int quantityToAdd) {
        productFile.addProduct(productId, quantityToAdd);
    }

    public void removeProduct(int productId, int quantityToRemove) {
        productFile.removeProduct(productId, quantityToRemove);
    }

    public int getLocationId() {
        return location.id();
    }
}
