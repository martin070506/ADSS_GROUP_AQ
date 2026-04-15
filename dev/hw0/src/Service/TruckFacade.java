package Service;

import Domain.Driver;
import Domain.Truck;

import java.util.*;

public class TruckFacade {
    private List<Truck> trucks;
    private List<Truck> availableTrucks;
    private List<Driver> drivers;
    private List<Driver> availableDrivers;
    private Scanner reader = new Scanner(System.in);
    public TruckFacade(List<Truck> trucks, List<Driver> drivers) {
        this.trucks = trucks;
        this.drivers = drivers;
        this.availableTrucks = new ArrayList<>(this.trucks);
        this.availableDrivers = new ArrayList<>(this.drivers);
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
    public void takeDriver(Driver driver){
        availableDrivers.remove(driver);
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
            index++;
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
        System.out.println("Truck Number: " + truck.getTruckNumber() + '\n'+
                "Truck allowed Weight: " + (truck.getMaxWeight()- truck.getCurrentWeight())+'\n'+
                "Truck min License: " + truck.getMinLicense()+ '\n'+
                "Truck Model: " +truck.getModel()+'\n');
    }

    public Driver chooseDriver(){
        int index = 0;

        if(availableDrivers.isEmpty()){
            System.out.println("There are no Drivers available");
            return null;
        }

        for (Driver driver : availableDrivers){
            System.out.println(index+":");
            System.out.println("--------------");
            displayDriver(driver);
            System.out.println("--------------");
            index++;
        }

        System.out.println("###  Now choose one of them  ###");
        int choice=reader.nextInt();
        while (choice<0 || choice>availableDrivers.size()-1){
            System.out.println("Invalid choice");
            choice=reader.nextInt();
        }
        return availableDrivers.get(choice);
    }

    private void displayDriver(Driver driver){
        System.out.println("Driver name: " + driver.driverName() + '\n' +
                "License: " + driver.license()+ '\n');
    }
}
