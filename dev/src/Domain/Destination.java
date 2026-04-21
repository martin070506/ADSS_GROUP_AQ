package Domain;

import java.util.List;

public class Destination   {
    private final Location location;
    private final ProductFile productFile;
    private boolean wasVisited=false;

    public Destination(Location location, ProductFile productFile){
        this.location = location;
        this.productFile = productFile;
    }

    public void handleShipment(Truck truck) {
        handleShipment(truck, null);
    }

    public void handleShipment(Truck truck, List<ProductPair> addedProducts) {

        List<ProductPair> requestedProducts = productFile.getProducts();
        truck.removeProducts(requestedProducts, addedProducts);
        wasVisited = true;
    }

    public String getContactName(){
        return location.contactName();
    }
    public ProductFile getProductFile() { return productFile; }

    public List<ProductPair> getProducts() {
        return productFile.getProducts();
    }

    public Location getLocation() {
        return location;
    }

    public boolean isVisited() {
        return wasVisited;
    }

    @Override
    public String toString() {
        String result = "Destination: " + location.address() +
                " | Contact: " + location.contactName() +
                " | File #" + productFile.getFileNumber() + "\n";

        result += "Products to deliver:\n";

        if (productFile.getProducts() == null || productFile.getProducts().isEmpty()) {
            result += "  (No products assigned)";
        } else {
            for (int i = 0; i < productFile.getProducts().size(); i++) {
                result += "  " + (i + 1) + ". " + productFile.getProducts().get(i).toString() + "\n";
            }
        }
        return result;
    }
}
