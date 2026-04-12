package Domain;

import java.util.List;

public class Supplier{

    Location supplierLocation;
    List<ProductPair> products;

    public Supplier(Location location, List<ProductPair> products){
        this.supplierLocation = location;
        this.products = products;
    }

    public void handleShipment(Transport transport) {
        /// this method will add his inventory to the currently held items of the transport
    }

}
