package Service;

import Domain.Truck;
import java.util.ArrayList;
import java.util.List;

public class TruckService {
    private final List<Truck> trucks;

    public TruckService() { this.trucks = new ArrayList<>(); }
    public TruckService(List<Truck> trucks) { this.trucks = new ArrayList<>(trucks); }

    public void addTruck(int truckNumber,String model,int truckWeight,int MaxWeight,int requiredLicense)
    {
        trucks.add(new Truck(truckNumber,model,truckWeight,MaxWeight,requiredLicense));
    }

    // FIXED: Direct choice indexing selection
    public Truck getAvailableTruckByIndex(int index) {
        List<Truck> available = getAvailableTrucksList(Integer.MAX_VALUE);
        if (index >= 0 && index < available.size()) {
            return available.get(index);
        }
        throw new IllegalArgumentException("Truck choice out of bounds.");
    }

    public Truck getAvailableTruckByLicenseIndex(int index, int minLicense) {
        List<Truck> available = getAvailableTrucksList(minLicense);
        if (index >= 0 && index < available.size()) {
            return available.get(index);
        }
        throw new IllegalArgumentException("Truck choice out of bounds for license scope.");
    }

    private List<Truck> getAvailableTrucksList(int minLicense) {
        List<Truck> available = new ArrayList<>();
        for (Truck t : trucks) if (t.isAvailable() && t.getMinLicense() <= minLicense) available.add(t);
        return available;
    }

    public List<String> getAvailableTrucksDisplay() { return getAvailableTrucksDisplay(Integer.MAX_VALUE); }
    public List<String> getAvailableTrucksDisplay(int minLicense) {
        List<String> display = new ArrayList<>();
        for (Truck t : getAvailableTrucksList(minLicense)) display.add(t.toString());
        return display;
    }
}