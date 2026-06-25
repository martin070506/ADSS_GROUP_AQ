package Domain.Transportation;

import java.time.LocalDate;
import java.util.*;

public class TransportFile {

    private String transportLog;
    private String trucksLog;
    private final String driversLog;
    private String suppliersLog;
    private String requestsLog;

    public TransportFile(LocalDate departureTime, String truckInfo, String driverInfo, String sourceInfo) {
        transportLog = "Source: " + sourceInfo + '\n'+
                "Departure Time: " + departureTime + '\n';
        trucksLog = truckInfo;
        driversLog = driverInfo;
        requestsLog = "";
        suppliersLog = "";
    }

    public void leaveSupplier(String supplierName, String supplierInfo, int weight) {
        transportLog += "Left Supplier " + supplierName + ", Truck Weight : " + weight + '\n';
        suppliersLog += supplierInfo + '\n';
    }

    public void arriveAtSupplier(String supplierName) {
        transportLog += "Arrived at Supplier " + supplierName + '\n';
    }

    public void skipSupplier(String supplierName, String reason) {

        transportLog += "Skipped Supplier " + supplierName + " due to " + reason + '\n';
    }

    public void overWeightAlert(int weight) {

        transportLog += "Over Weight Alert, Truck Weight : " + weight + '\n';
    }

    public void changeTruck(int truckCapacity, String truckInfo){
        transportLog += "Truck swapped, New capacity: " + truckCapacity + '\n';
        trucksLog = " (Swapped)\n" + truckInfo;
    }

    public void skipRequest(String requestName, String reason) {

        transportLog += "Skipped Request " + requestName + " due to " + reason + '\n';
    }

    public void arriveAtRequest(String requestName) {
        transportLog += "Arrived at Request " + requestName + '\n';
    }

    public void leaveRequest(String requestName, String requestInfo) {
        transportLog += "Left Request " + requestName + '\n';
        requestsLog += requestInfo;
    }

    public void UpdateDropOff(String productName, int amountToRemove) {
        transportLog += "Dropped off " + amountToRemove + " " + productName + ".\n";
    }

    public String toString(String itemsLeft) {

        StringBuilder sb = new StringBuilder();

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
        sb.append(itemsLeft != null ? itemsLeft : "No items currently held.").append("\n");

        return sb.toString();
    }
}
