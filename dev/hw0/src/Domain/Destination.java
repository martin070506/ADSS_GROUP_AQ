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

        System.out.println("Successfully unloaded " + requestedProducts.size() +
                " types of products at " + location.address());
    }

//    private int calculateWeightOfDropOff() {
//        int sum = 0;
//        for (ProductPair pair : productFile.getProducts())
//            sum += pair.product.weight() * pair.getAmount();
//
//        return sum;
//    }

    public String getContactName(){
        return location.contactName();
    }

    public List<ProductPair> getProducts() {
        return productFile.getProducts();
    }
}
