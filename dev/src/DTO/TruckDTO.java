package DTO;

import Domain.Transportation.Truck;

public record TruckDTO(int id, int truckNumber, String model, int startWeight, int maxWeight, int minLicense) {

    public TruckDTO(Truck truck) {
        this(
                truck.getId(),
                truck.getTruckNumber(),
                truck.getModel(),
                truck.getStartWeight(),
                truck.getMaxWeight(),
                truck.getMinLicense()
        );
    }
}