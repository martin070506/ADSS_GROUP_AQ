import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CompanyManager {
    private int globalFileNumber=0;
    private TransportStorageBase transportStorage;
    private TruckController truckController;
    private ShipmentController shipmentController;
    private List<Destination> dropOffDestinations = new LinkedList<>();

    private static CompanyManager instance = null;

    private CompanyManager(List<Supplier> suppliers,List<Truck> trucks,List<Driver> drivers) {
        truckController = new TruckController(trucks,drivers);
        shipmentController = new ShipmentController(suppliers);
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
       while (!truckAndDriverMatch){
           System.out.println("Choose a Truck and a matching Driver");
           Truck t=truckController.chooseTruck();
           Driver d=truckController.chooseDriver();
           truckAndDriverMatch=t.getMinLicense()<=d.getLicense();
           if(!truckAndDriverMatch) System.out.println("Truck and Driver chosen are not matching");
       }


    }
}
