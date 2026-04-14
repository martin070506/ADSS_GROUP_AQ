package Domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class TransportFile {

    private String text;

    public TransportFile(LocalDate departureTime) {
        text = "Departure Time: " + departureTime + '\n';
    }
    public void leaveSupplier(int weight){
        text=text+"Left Supplier, current Weight:" +weight+'\n';
    }
    public void overWeightAlert(int weight){
        text=text+"Over Weight Alert, current Weight:" +weight+'\n';
    }

    void removeLocation(Location location){

    }

    void changeLocation(Location toRemove, Location toAdd){

    }

    void changeTruck(Truck toRemove, Truck toAdd){

    }

    void removeProducts(List<ProductPair> products){

    }


}
