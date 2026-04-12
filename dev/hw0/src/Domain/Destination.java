package Domain;

public class Destination   {
    private Location location;
    private ProductFile productFile;

    public Destination(Location location, ProductFile productFile){
        this.location = location;
        this.productFile = productFile;
    }

    public void handleShipment(Transport transport) {
        System.out.println("Shipment successful");
    }

    public int calculateWeightOfDropOff() {
        int sum = 0;
        for (ProductPair pair : productFile.getProducts())
            sum += pair.getProduct().getWeight() * pair.getAmount();

        return sum;
    }
}
