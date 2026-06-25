package Service.Transportation;

import DAO.Transportation.TruckDAO;
import DTO.Transportation.TruckDTO;
import Domain.Transportation.Truck;
import Exceptions.OverweightException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TruckService {
    private final List<Truck> trucks;
    private int counter;
    private final ProductCatalogService productService;
    private final TruckDAO truckDAO;

    public TruckService(ProductCatalogService productService, TruckDAO truckDAO) {
        this.productService = productService;
        this.trucks = new ArrayList<>();
        this.truckDAO = truckDAO;
        this.counter = truckDAO.getHighestTruckID() + 1;
    }

    public void loadAllTrucksFromDB() {
        this.trucks.clear();
        List<TruckDTO> dtos = truckDAO.loadAllTrucks();

        for (TruckDTO dto : dtos) {
            this.trucks.add(new Truck(
                    dto.id(),
                    dto.truckNumber(),
                    dto.model(),
                    dto.startWeight(),
                    dto.maxWeight(),
                    dto.minLicense()
            ));
        }
    }

    public void addTruck(int truckNumber, String model, int truckWeight, int maxWeight, int requiredLicense) {
        int count = counter++;
        Truck truck = new Truck(count, truckNumber, model, truckWeight, maxWeight, requiredLicense);
        trucks.add(truck);

        TruckDTO dto = new TruckDTO(count, truckNumber, model, truckWeight, maxWeight, requiredLicense);
        truckDAO.addTruck(dto);
    }

    public Truck getTruck(int truckId) {
        for (Truck truck : trucks)
            if (truck.getId() == truckId)
                return truck;

        throw new IllegalArgumentException("Truck not found: " + truckId);
    }

    public List<Integer> getBiggerTruckIds(int minLicense, int truckId) {
        int maxWeight = 0;
        if (truckId != -1)
            maxWeight = getTruck(truckId).getMaxWeight();

        List<Integer> available = new ArrayList<>();
        for (Truck truck : trucks)
            if (truck.isAvailable() && truck.getMinLicense() <= minLicense)
                if (maxWeight < truck.getMaxWeight())
                    available.add(truck.getId());

        return available;
    }

    public String getTruckDisplay(int truckId) {
        return getTruck(truckId).toString();
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
        Truck oldTruck = getTruck(truckId);
        Truck replacement = getTruck(newTruckId);

        oldTruck.transferHoldingsToOtherTruck(replacement);
        oldTruck.emptyTruck();

        oldTruck.setAvailable(true);
        replacement.setAvailable(false);
    }

    public Map<Integer, Integer> getTruckProducts(int truckId) {
        return getTruck(truckId).getLoadedProducts();
    }

    public List<Integer> getAvailableTruckIds() {
        List<Integer> available = new ArrayList<>();
        for (Truck truck : trucks) {
            if (truck.isAvailable()) {
                available.add(truck.getId());
            }
        }
        return available;
    }

    public boolean isDriverEligible(int license, int truckId) {
        return getTruck(truckId).getMinLicense() <= license;
    }

    public void addProductToTruck(int truckId, Map<Integer, Integer> itemsToLoad) {
        Truck truck = getTruck(truckId);
        truck.addProducts(itemsToLoad);
        if (getTruckWeight(truckId) > truck.getMaxWeight())
            throw new OverweightException(getTruckWeight(truckId), truck.getMaxWeight());
    }

    public boolean isTruckOverweight(int truckId) {
        return getTruckWeight(truckId) > getTruckMaxWeight(truckId);
    }
}