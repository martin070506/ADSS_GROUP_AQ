package Service.Transportation;

import DAO.TruckDAO;
import DTO.TruckDTO;
import Domain.Transportation.Truck;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TruckService {
    private final List<Truck> trucks;
    private int counter = 0;
    private final ProductCatalogService productService;
    private final TruckDAO truckDAO;


    public TruckService(ProductCatalogService productService, TruckDAO truckDAO) throws SQLException {
        this.productService = productService;
        this.trucks = new ArrayList<>();
        this.counter=truckDAO.getHighestTruckID()+1;//adding 1 to start from a new ID
        this.truckDAO = truckDAO;
    }
    public void loadAllTrucksFromDB() throws SQLException {
        this.trucks.addAll(truckDAO.loadAllTrucks());
    }


    public void addTruck(int truckNumber,String model,int truckWeight,int MaxWeight,int requiredLicense) throws SQLException {
        int count=counter++;
        Truck t =new Truck(count, truckNumber, model, truckWeight, MaxWeight, requiredLicense);
        TruckDTO tDTO=new TruckDTO(count, truckNumber, model, truckWeight, MaxWeight, requiredLicense);
        trucks.add(t);
        truckDAO.addTruck(tDTO);

    }

    public Truck getTruck(int truckId) {
        for (Truck truck : trucks)
            if (truck.getId() == truckId)
                return truck;

        throw new IllegalArgumentException("Truck not found: " + truckId);
    }

    public List<Integer> getBiggerTruckIds(int minLicense, int truckId) {
        int maxWeight = getTruckWeight(truckId);
        List<Integer> available = new ArrayList<>();
        for (Truck truck : trucks)
            if (truck.isAvailable() && truck.getMinLicense() <= minLicense && truck.getMaxWeight() >= maxWeight)
                available.add(truck.getId());

        return available;
    }

    public String getTruckDisplay(int truckId) {
        for (Truck truck : trucks)
            if (truck.getId() == truckId)
                return truck.toString();

        throw new IllegalArgumentException("Truck not found: " + truckId);
    }

    public void emptyTruck(int truckId) {
        Truck truck = getTruck(truckId);
        truck.emptyTruck();
    }

    public int getTruckWeight(int truckId) {
        Truck truck = getTruck(truckId);
        int weight = truck.getStartWeight();
        Map<Integer, Integer> loadedProducts = truck.getLoadedProducts();
        for (Map.Entry<Integer, Integer> entry : loadedProducts.entrySet())
            weight += productService.getWeightForProduct(entry.getKey()) * entry.getValue();

        return weight;
    }

    public void removeProducts(Map<Integer, Integer> products, int truckId) {
        Truck truck = getTruck(truckId);
        truck.removeProducts(products);
    }

    public int getTruckMaxWeight(int truckId) {
        return getTruck(truckId).getMaxWeight();
    }

    public void replaceTrucks(int truckId, int newTruckId) {
        Truck truck = getTruck(truckId);
        Truck replacement = getTruck(newTruckId);
        replacement.transferHoldingsToOtherTruck(truck);
        truck.emptyTruck();
        truck.setAvailable(true);
        replacement.setAvailable(false);
    }

    public Map<Integer, Integer> getTruckProducts(int truckId) {
        return getTruck(truckId).getLoadedProducts();
    }

    public List<Integer> getAvailableTruckIds() {
        List<Integer> available = new ArrayList<>();
        for (Truck truck : trucks)
            if (truck.isAvailable())
                available.add(truck.getId());

        return available;
    }

    public boolean isDriverEligable(int license, int truckId) {
        for (Truck truck : trucks)
            if (truck.getId() == truckId)
                return truck.getMinLicense() <= license;

        throw new IllegalArgumentException("Truck not found: " + truckId);
    }
}