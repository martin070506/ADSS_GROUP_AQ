package Domain;

import java.time.LocalDate;
import java.util.*;

public class TransportFile {

    private String text;
    private List<Supplier> suppliers;
    private List<Destination> destinations;
    private Map<Product,Integer> totalProductsNeeded;
    private Truck truck;
    private Driver driver;
    private Location source;
    public TransportFile(LocalDate departureTime, Transport transport) {
        source=transport.getSource();
        text = "Source: " + source.address()+ '\n'+
                "Departure Time: " + departureTime + '\n';

        /// we need copy constructors
        suppliers = new ArrayList<>(transport.getSuppliers());
        destinations = new ArrayList<>(transport.getDestinations());
        totalProductsNeeded = aggregateProductsToInteger(transport.getSupplierAllocations());

        truck = transport.getTruck();
        driver = transport.getDriver();
        source = transport.getSource();
    }
    public void leaveSupplier(int weight) {
        text += "Left Supplier, Truck Weight : " +weight+'\n';
    }

    public void overWeightAlert(int weight){
        text += "Over Weight Alert, Truck Weight : " +weight+'\n';
    }

    public void removeLocation(Supplier supplier){
        suppliers.remove(supplier);
    }

    public Map<Product,Integer> getTotalProductsNeeded() {
        return totalProductsNeeded;
    }

    public List<Supplier> getSuppliers(){
        return suppliers;
    }

    public List<Destination> getDestinations(){
        return destinations;
    }

    public void removeDestination(Destination destination){
        destinations.remove(destination);
    }

    public void changeTruck(Truck toAdd){
        this.truck = toAdd;
    }

    private Map<Product, Integer> aggregateProductsToInteger(Map<Supplier, List<ProductPair>> supplierAllocations) {
        Map<Product, Integer> totalProductCounts = new HashMap<>();

        for (List<ProductPair> productList : supplierAllocations.values()) {
            if (productList == null) continue;

            for (ProductPair pair : productList) {
                Product product = pair.product;
                int amount = pair.getAmount();

                // Merge the amount into the map using the Product object as the key
                totalProductCounts.put(product, totalProductCounts.getOrDefault(product, 0) + amount);
            }
        }

        return totalProductCounts;
    }

    public void removeProductsFromAggregate(List<ProductPair> products){
        if (products == null) return;
        for (ProductPair p: products){
            int currentAmount = totalProductsNeeded.getOrDefault(p.product, 0);
            int newAmount = currentAmount - p.getAmount();

            if (newAmount <= 0) {
                totalProductsNeeded.remove(p.product);
            } else {
                totalProductsNeeded.put(p.product, newAmount);
            }
        }
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
        appendSectionHeader(sb, "SUPPLIERS THAT WERE VISITED");
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

        boolean hasItems = false;
        for (Map.Entry<String, Integer> entry : totals.entrySet()) {
            if (entry.getValue() > 0) {
                sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" units\n");
                hasItems = true;
            }
        }

        if (!hasItems) {
            sb.append("No items currently held.\n");
        }
    }

    private Map<String, Integer> getAggregatedInventory() {
        Map<String, Integer> totals = new HashMap<>();
        for (Product p : totalProductsNeeded.keySet()) {
            totals.put(p.name(),totalProductsNeeded.get(p));
        }
        return totals;
    }



}
