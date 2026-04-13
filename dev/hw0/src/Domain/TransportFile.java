package Domain;

import java.util.Date;
import java.util.List;

public class TransportFile {

    private String text;

    public TransportFile(){
        text="";
    }
    public void leaveSupplier(int weight){
        text=text+"Left Supplier, current Weight:" +weight+'\n';
    }
    public void overWeightAlert(int weight){
        text=text+"Over Weight Alert, current Weight:" +weight+'\n';
    }

    public void addDate(Date date){
        text=text+"Departure Time: "+date+'\n';
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
