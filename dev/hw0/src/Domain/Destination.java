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

        List<ProductPair> requestedProducts = productFile.getProducts();
        truck.removeProducts(requestedProducts); /// throws Exception if not enough stock
        wasVisited=true;
    }

    public String getContactName(){
        return location.contactName();
    }

    public List<ProductPair> getProducts() {
        return productFile.getProducts();
    }

    public Location getLocation() {
        return location;
    }

    public boolean isVisited() {
        return wasVisited;
    }
}
