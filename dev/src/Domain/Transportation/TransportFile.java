package Domain.Transportation;

import Domain.Workers.Driver;

import java.time.LocalDate;
import java.util.*;

public class TransportFile {

    private String transportLog;
    private String trucksLog;
    private final String driversLog;
    private String suppliersLog;
    private String requestsLog;

    public TransportFile(LocalDate departureTime, Truck truck, String driverInfo, Location source) {
        transportLog = "Source: " + source.toString() + '\n'+
                "Departure Time: " + departureTime + '\n';
        trucksLog = truck.toString();
        driversLog = driverInfo;
        requestsLog = "";
        suppliersLog = "";
    }

    public void leaveSupplier(Supplier supplier, int weight) {
        transportLog += "Left Supplier " + supplier.getName() + ", Truck Weight : " + weight + '\n';
        suppliersLog += supplier.getSupplierLocation().toString() + '\n';
    }

    public void arriveAtSupplier(Supplier supplier) {
        transportLog += "Arrived at Supplier " + supplier.getName() + '\n';
    }

    public void skipSupplier(Supplier supplier) {

        transportLog += "Skipped Supplier " + supplier.getName() + '\n';
    }

    public void overWeightAlert(int weight) {

        transportLog += "Over Weight Alert, Truck Weight : " + weight + '\n';
    }

    public void changeTruck(Truck truck){
        transportLog += "Truck swapped, New capacity: " + truck.getMaxWeight() + '\n';
        trucksLog = " (Swapped)\n" + truck.toString();
    }

    public void changeDriver(String driverName) {
        transportLog += "Driver swapped, Name: " + driverName;
        trucksLog = " (Swapped)\n" + driverName;
    }

    public void skipRequest(Request request) {
        transportLog += "Skipped Request " + request.getContactName() + '\n';
    }

    public void arriveAtRequest(Request request) {
        transportLog += "Arrived at Request " + request.getContactName() + '\n';
    }

    public void leaveRequest(Request request) {
        transportLog += "Left Request " + request.getContactName() + '\n';
        requestsLog += request.toString();
    }

    public String toString(Map<Product, Integer> itemsLeft) {

        StringBuilder sb = new StringBuilder();

        sb.append("Transport File :\n\n");

        sb.append("--- TRANSPORT LOG ---\n");
        sb.append(transportLog != null ? transportLog : "").append("\n");

        sb.append("--- TRUCK DETAILS ---\n");
        sb.append(trucksLog != null ? trucksLog : "").append("\n\n");

        sb.append("--- DRIVER DETAILS ---\n");
        sb.append(driversLog != null ? driversLog : "").append("\n\n");

        sb.append("--- SUPPLIERS THAT WERE VISITED ---\n");
        sb.append(suppliersLog != null ? suppliersLog : "").append("\n\n");

        sb.append("--- SCHEDULED REQUESTS ---\n");
        sb.append(requestsLog != null ? requestsLog : "").append("\n\n");

        sb.append("--- ITEM LEFT ON TRUCK ---\n");
        if (itemsLeft != null && !itemsLeft.isEmpty()) {
            for (Map.Entry<Product, Integer> entry : itemsLeft.entrySet()) {
                sb.append("- ").append(entry.getKey().name()).append(": ").append(entry.getValue()).append(" units\n");
            }
        } else {
            sb.append("No items currently held.\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(null);
    }
}
