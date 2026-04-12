package Domain;

public class Truck {

    private int truckNumber;
    private String model;
    private int truckWeight;
    private int maxWeight;
    private int minLicense;

    public Truck(int truckNumber, String model, int truckWeight, int maxWeight, int minLicense) {
        this.truckNumber = truckNumber;
        this.model = model;
        this.truckWeight = truckWeight;
        this.maxWeight = maxWeight;
        this.minLicense = minLicense;
    }

    public int getTruckNumber() {
        return truckNumber;
    }
    public void setTruckNumber(int truckNumber) {
        this.truckNumber = truckNumber;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getTruckWeight() {
        return truckWeight;
    }
    public void setTruckWeight(int truckWeight) {
        this.truckWeight = truckWeight;
    }
    public int getMaxWeight() {
        return maxWeight;
    }
    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }
    public int getMinLicense() {
        return minLicense;
    }
    public void setMinLicense(int minLicense) {
        this.minLicense = minLicense;
    }
}
