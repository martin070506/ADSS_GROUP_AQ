package Domain;

import java.time.LocalDate;
import java.util.*;

public class TransportFile {

    private String text;
    private List<Supplier> suppliers;
    private List<Destination> destinations;
    private Map<Supplier,List<ProductPair>> supplierAllocations;
    private Truck truck;
    private Driver driver;
    public TransportFile(LocalDate departureTime, Transport transport) {
        text = "Departure Time: " + departureTime + '\n';
        /// we need copy constructors
        suppliers = new ArrayList<>(transport.getSuppliers());
        destinations = new ArrayList<>(transport.getDestinations());
        supplierAllocations = new HashMap<>(transport.getSupplierAllocations());
        truck = transport.getTruck();
        driver = transport.getDriver();
    }
    public void leaveSupplier(int weight){
        text=text+"Left Supplier, current Weight:" +weight+'\n';
    }

    public void overWeightAlert(int weight){
        text=text+"Over Weight Alert, current Weight:" +weight+'\n';
    }

    public void removeLocation(Supplier supplier){
        suppliers.remove(supplier);

    }

    public void removeDestination(Destination destination){
        destinations.remove(destination);
    }



    void changeTruck(Truck toAdd){
        this.truck = toAdd;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Using helpers to build each section
        appendSectionHeader(sb, "TRANSPORT LOG");
        sb.append(text).append("\n");

        appendTruckDetails(sb);
        appendSupplierDetails(sb);
        appendDestinationDetails(sb);
        appendCombinedInventory(sb);

        return sb.toString();
    }

// --- PRIVATE HELPER METHODS ---

    private void appendSectionHeader(StringBuilder sb, String title) {
        sb.append("--- ").append(title).append(" ---\n");
    }

    private void appendTruckDetails(StringBuilder sb) {
        appendSectionHeader(sb, "TRUCK DETAILS");
        if (truck != null) {
            sb.append("ID: ").append(truck.getTruckNumber())
                    .append(" | Max Capacity: ").append(truck.getMaxWeight()).append("\n");
        } else {
            sb.append("No truck assigned.\n");
        }
        sb.append("\n");
    }

    private void appendSupplierDetails(StringBuilder sb) {
        appendSectionHeader(sb, "SCHEDULED SUPPLIERS");
        for (Supplier s : suppliers) {
            Location loc = s.getSupplierLocation();
            sb.append("- ").append(loc.contactName())
                    .append(" | Address: ").append(loc.address()).append("\n");
        }
        sb.append("\n");
    }

    private void appendDestinationDetails(StringBuilder sb) {
        appendSectionHeader(sb, "SCHEDULED DESTINATIONS");
        for (Destination d : destinations) {
            Location loc = d.getLocation();
            sb.append("- ").append(loc.contactName())
                    .append(" | Address: ").append(loc.address()).append("\n");
        }
        sb.append("\n");
    }

    private void appendCombinedInventory(StringBuilder sb) {
        appendSectionHeader(sb, "TOTAL ITEMS HELD");
        Map<String, Integer> totals = getAggregatedInventory();

        if (totals.isEmpty()) {
            sb.append("No items currently held.\n");
        } else {
            totals.forEach((name, amount) ->
                    sb.append("- ").append(name).append(": ").append(amount).append(" units\n")
            );
        }
    }


    private Map<String, Integer> getAggregatedInventory() {
        Map<String, Integer> totals = new HashMap<>();
        for (List<ProductPair> pairs : supplierAllocations.values()) {
            for (ProductPair pair : pairs) {
                String name = pair.product.name();
                totals.put(name, totals.getOrDefault(name, 0) + pair.getAmount());
            }
        }
        return totals;
    }



}
