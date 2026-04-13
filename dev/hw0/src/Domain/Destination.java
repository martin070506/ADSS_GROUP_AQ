package Domain;

import java.util.List;

public class Destination   {
    private Location location;
    private ProductFile productFile;

    public Destination(Location location, ProductFile productFile){
        this.location = location;
        this.productFile = productFile;
    }

    public String getContactName(){
        return location.getContactName();
    }

    public void handleShipment(Transport transport) {
        boolean result=transport.removeItems(getProducts(),calculateWeightOfDropOff());
        if(!result){
            System.out.println("Shipment unsuccessful, not all products available, therefore NOTHING WAS DROPPED OFF (DEFAULT CHOICE)");
        }
        else{
            System.out.println("Shipment successful to: " + location.getContactName());
            location.addProductsAvailable(getProducts());

        }

    }

    private int calculateWeightOfDropOff() {
        int sum = 0;
        for (ProductPair pair : productFile.getProducts())
            sum += pair.getProduct().getWeight() * pair.getAmount();

        return sum;
    }

    public List<ProductPair> getProducts() {
        return productFile.getProducts();
    }
}
