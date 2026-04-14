package Service;

import Domain.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CompanyManager {
    private int globalFileNumber = 0;
    private final TruckFacade truckFacade;
    private final ShipmentFacade shipmentFacade;
    private final List<Destination> dropOffDestinations = new LinkedList<>();
    private final List<Location> locations;


    private static CompanyManager instance = null;

    private CompanyManager(List<Truck> trucks, List<Driver> drivers, List<Supplier> suppliers,
                           List<Location> locations) {
        this.truckFacade = new TruckFacade(trucks, drivers);
        this.shipmentFacade = new ShipmentFacade(suppliers);
        this.locations = locations;
    }

    public static CompanyManager getInstance(List<Truck> trucks,List<Driver> drivers,
                                             List<Supplier> suppliers, List<Location> locations) {
        if (instance == null)
            instance = new CompanyManager(trucks, drivers, suppliers, locations);

        return instance;
    }

    public static CompanyManager getInstance() {
        return getInstance(List.of(), List.of(), List.of(), List.of());
    }

    public void addDestination(Location storeLocation, List<ProductPair> neededItems){
        ProductFile productFile = new ProductFile(neededItems, ++globalFileNumber);
        Destination destination = new Destination(storeLocation, productFile);
        dropOffDestinations.add(destination);
    }

    public void startShipment(){

       boolean truckAndDriverMatch = false;
       Truck truck = null;
       Driver driver = null;
       while (!truckAndDriverMatch){
           System.out.println("Choose a Truck and a matching Driver");
           truck = truckFacade.chooseTruck();
           driver = truckFacade.chooseDriver();
           truckAndDriverMatch = truck.getMinLicense() <= driver.license();
           if (!truckAndDriverMatch)
               System.out.println("Truck and Driver chosen are not matching");
       }

       Location source = selectSourceLocation();
       truckFacade.removeTruck(truck);
       truckFacade.removeDriver(driver);
       shipmentFacade.startShipment(truck, driver, source, dropOffDestinations, truckFacade.getTrucks());

    }

    private Location selectSourceLocation() {

        Scanner reader = new Scanner(System.in);

        System.out.println("\n--- Select Source Location ---");

        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);
            System.out.println((i + 1) + ". " + location.toString());
        }

        while (true) {
            System.out.print("Enter location index: ");
            String input = reader.nextLine().trim();

            try {
                int index = Integer.parseInt(input) - 1;

                if (index >= 0 && index < locations.size()) {
                    Location selected = locations.get(index);
                    System.out.println("Selected: " + selected.address());
                    return selected;
                }
                else
                    System.out.println("Please choose between 1 and " + locations.size());

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
}
