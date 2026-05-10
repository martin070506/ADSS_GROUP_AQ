package Domain;

import java.util.List;

public class TruckFacade {
    private List<Truck> trucks;
    private List<Truck> availableTrucks;


    public TruckFacade(List<Truck> trucks) {
        this.trucks = trucks;
        this.availableTrucks = trucks;
    }



    public List<Truck> getTrucks(){
        return trucks;
    }
    public void addTruck(Truck truck){
        trucks.add(truck);
        availableTrucks.add(truck);
    }
    public void removeTruck(Truck truck){
        trucks.remove(truck);
        availableTrucks.remove(truck);
    }

    public void takeTruck(Truck truck){
        availableTrucks.remove(truck);
    }


    public List<Truck> getAvailableTrucks() {
        return availableTrucks;
    }


}
