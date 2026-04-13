package Service;

import Domain.*;

import java.util.LinkedList;
import java.util.List;

public class CompanyManager {
    private int globalFileNumber=0;
    private TruckFacade truckFacade;
    private ShipmentFacade shipmentFacade;
    private List<Destination> dropOffDestinations = new LinkedList<>();
    private List<Supplier> suppliers = new LinkedList<>();


    private static CompanyManager instance = null;

    private CompanyManager(List<Supplier> suppliers,List<Truck> trucks,List<Driver> drivers) {
        this.truckFacade = new TruckFacade(trucks,drivers);
        this.shipmentFacade = new ShipmentFacade(suppliers);
        this.suppliers = suppliers;
    }

    public static CompanyManager getInstance(List<Supplier> suppliers,List<Truck> trucks,List<Driver> drivers) {
        if (instance == null)
            instance = new CompanyManager(new  LinkedList<Supplier>(),new LinkedList<Truck>(),new LinkedList<Driver>());

        return instance;
    }

    public static CompanyManager getInstance(){
        if (instance == null)
            instance = new CompanyManager(new  LinkedList<Supplier>(),new LinkedList<Truck>(),new LinkedList<Driver>());

        return instance;
    }

    public void addDestination(Location storeLocation, List<ProductPair> neededItems){
        ProductFile productFile = new ProductFile(neededItems, ++globalFileNumber);
        Destination destination = new Destination(storeLocation, productFile);
        dropOffDestinations.add(destination);
    }

    public void startShipment(){
       boolean truckAndDriverMatch=false;
       Truck truck=null;
       Driver driver=null;
       while (!truckAndDriverMatch){
           System.out.println("Choose a Truck and a matching Driver");
           truck=truckFacade.chooseTruck();
           driver=truckFacade.chooseDriver();
           truckAndDriverMatch=truck.getMinLicense()<=driver.getLicense();
           if(!truckAndDriverMatch) System.out.println("Truck and Driver chosen are not matching");
       }

       truckFacade.removeTruck(truck);
       truckFacade.removeDriver(driver);
       shipmentFacade.startShipment(truck,driver,this.dropOffDestinations,truckFacade.getTrucks());




    }
}
