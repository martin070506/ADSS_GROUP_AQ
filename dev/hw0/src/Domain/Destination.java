package Domain;

import java.util.List;

public class Destination   {
    private final Location location;
    private final ProductFile productFile;

    public Destination(Location location, ProductFile productFile){
        this.location = location;
        this.productFile = productFile;
    }


    public void handleShipment(Truck truck) {

        List<ProductPair> requestedProducts = productFile.getProducts();
        truck.removeProducts(requestedProducts);
    }

    public String getContactName(){
        return location.contactName();
    }

    public List<ProductPair> getProducts() {
        return productFile.getProducts();
    }
}
