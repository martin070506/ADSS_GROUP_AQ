package Service;

import Domain.Driver;
import Domain.Truck;

import java.util.*;

public class TruckController {
    private List<Truck> trucks;
    private List<Truck> availableTrucks;
    private List<Driver> drivers;
    private List<Driver> availableDrivers;
    private Scanner reader = new Scanner(System.in);
    public TruckController(List<Truck> trucks, List<Driver> drivers) {
        this.trucks = trucks;
        this.drivers = drivers;
        this.availableTrucks = List.copyOf(trucks);
        this.availableDrivers = List.copyOf(drivers);


    }




    public void addTruck(Truck truck){
        trucks.add(truck);
        availableTrucks.add(truck);
    }
    public void removeTruck(Truck truck){
        trucks.remove(truck);
        availableTrucks.remove(truck);
    }
    public void addDriver(Driver driver){
        drivers.add(driver);
        availableDrivers.add(driver);
    }
    public void removeDriver(Driver driver){
        drivers.remove(driver);
        availableDrivers.remove(driver);
    }

    public Truck chooseTruck(){
        int index = 0;

        if(availableTrucks.isEmpty()){
            System.out.println("There are no trucks available");
            return null;
        }

        for (Truck truck : availableTrucks){
            System.out.println(index+":");
            System.out.println("--------------");
            displayTruck(truck);
            System.out.println("--------------");
        }

        System.out.println("###  Now choose one of them  ###");
        int choice=reader.nextInt();
        while (choice<0 || choice>availableTrucks.size()-1){
            System.out.println("Invalid choice");
            choice=reader.nextInt();
        }
        return availableTrucks.get(choice);
    }

    private void displayTruck(Truck truck){
        System.out.println("Domain.Truck Number: " + truck.getTruckNumber() + '\n'+
                "Domain.Truck allowed Weight: " + (truck.getMaxWeight()- truck.getTruckWeight())+'\n'+
                "Domain.Truck min License: " + truck.getMinLicense()+ 'n'+
                "Domain.Truck Model: " +truck.getModel());
    }
}
