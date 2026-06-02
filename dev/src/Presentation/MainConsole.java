package Presentation;

import Exceptions.*;
import Service.*;

import java.util.*;

public class MainConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyManager companyManager;
    private final TransportManagerService transportService;
    private final SupplierService supplierService;
    private final ProductCatalogService productService;
    private final TruckService truckService;
    private final DriverService driverService;
    private final LocationService locationService;

    public MainConsole(CompanyManager companyManager, TransportManagerService transportService, SupplierService supplierService, ProductCatalogService productService, TruckService truckService, DriverService driverService, LocationService locationService) {
        this.companyManager = companyManager;
        this.transportService = transportService;
        this.supplierService = supplierService;
        this.productService = productService;
        this.truckService = truckService;
        this.driverService = driverService;
        this.locationService = locationService;
    }

    public void initiateShipment() {
        int truckIndex = chooseTruck();
        if (truckIndex == -1) return;

        int driverIndex = chooseDriver(truckIndex);
        if (driverIndex == -1) return;

        int sourceIndex = selectSourceLocation();
        if (sourceIndex == -1) return;

        Map<Integer, Map<Integer, Integer>> supplierAllocationsIds = chooseSuppliersAndProducts();

        if (supplierAllocationsIds.isEmpty()) return;

        try {
            int transportId = companyManager.createTransportAndGetId(truckIndex, driverIndex, sourceIndex, supplierAllocationsIds);
            processShipmentFlow(transportId);
        } catch (DomainException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Critical System Error: " + e.getMessage());
        }
    }

    private void processShipmentFlow(int transportId) {
        boolean shipmentFinish = false;
        while (!shipmentFinish) {
            try {
                companyManager.processTransport(transportId);
                shipmentFinish = true;
            } catch (OverweightException oe) {
                handleOverweightUI(transportId);
            } catch (InsufficientSupplierStockException | InsufficientTruckStockException ise) {
                System.out.println("Stock Problem: " + ise.getMessage());
                try { companyManager.handleStockException(transportId, ise); }
                catch (Exception e) { System.out.println("Error handling stock: " + e.getMessage()); }
            } catch (DomainException de) {
                System.out.println("General Domain Error: " + de.getMessage());
                break;
            } catch (Exception e) {
                System.out.println("General Error: " + e.getMessage());
                break;
            }
        }
        if (shipmentFinish) {
            System.out.println(companyManager.getTransportFileDisplayById(transportId));
            transportService.removeTransportById(transportId);
            System.out.println("Shipment finished successfully!");
        }
    }

    public void handleOverweightUI(int transportId) {
        String supplierName = companyManager.getFirstSupplierNameByTransportId(transportId);
        System.out.println("Truck is overweight at " + supplierName);
        System.out.println("1. Skip this supplier");
        System.out.println("2. Emergency Drop-off");
        System.out.println("3. Fine-tune: Remove specific items");
        System.out.println("4. Switch Truck");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        try {
            if (choice.equals("3")) {
                getItemsToRemoveUI(transportId);
            } else {
                companyManager.resolveOverweightIssue(transportId, choice);
            }
        } catch (NoDestinationForEmergencyDropOffException e) {
            System.out.println("No destination available for emergency drop-off.");
        } catch (Exception e) { System.out.println("Action failed: " + e.getMessage()); }
    }

    private void getItemsToRemoveUI(int transportId) {
        while (true) {
            Map<String, Integer> currentItemsDisplay = companyManager.getLoadedProductsDisplay(transportId);
            if (currentItemsDisplay.isEmpty()) {
                System.out.println("The truck is now empty!");
                break;
            }

            System.out.println("\nCurrent loaded items:");
            int i = 0;
            List<String> productNames = new ArrayList<>(currentItemsDisplay.keySet());
            for (String productName : productNames) {
                System.out.println("[" + (i+1) + "] " + productName + " (" + currentItemsDisplay.get(productName) + " units)");
                i++;
            }

            System.out.print("Enter product ID to remove (or type 'done'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;

            try {
                int productId = Integer.parseInt(input)-1;
                if (productId >= 0 && productId < productNames.size()) {
                    int amt = promptInt("Amount to remove: ");
                    if (amt > 0) {
                        companyManager.resolveOverweightWithFineTuning(transportId, productId, amt);
                        System.out.println("Items removed.");
                    } else { System.out.println("Invalid amount."); }
                } else { System.out.println("Product ID not found on truck."); }
            } catch (NumberFormatException e) { System.out.println("Invalid input. Please enter a number."); }
        }
    }

    private int chooseTruck() {
        List<String> trucks = truckService.getAvailableTrucksDisplay();
        if (trucks.isEmpty()) {
            System.out.println("No trucks available.");
            return -1;
        }

        System.out.println("\n--- Available Trucks ---");
        for (int i = 0; i < trucks.size(); i++)
            System.out.println("[" + (i+1) + "] " + trucks.get(i));
        while (true) {
            int choice = promptInt("Enter Truck: ")-1;
            if (choice >= 0 && choice < trucks.size()) return choice;
            System.out.println("Invalid Truck Index.");
        }
    }

    private int chooseDriver(int truckIndex) {
        List<String> drivers = driverService.getAvailableDriversDisplay();
        if (drivers.isEmpty()) {
            System.out.println("No drivers available.");
            return -1;
        }

        System.out.println("\n--- Available Drivers ---");
        for (int i = 0; i < drivers.size(); i++)
            System.out.println("[" + (i+1) + "] " + drivers.get(i));
        while (true) {
            int driverIndex = promptInt("Enter Driver: ")-1;
            if (driverIndex == -1)
                return -1;
            if (driverIndex >= 0 && driverIndex < drivers.size()){
                if (companyManager.checkDriverTruck(driverIndex, truckIndex))
                    return driverIndex;
                else
                    System.out.println("Driver is not eligible to this truck.");
            }
            else
                System.out.println("Invalid Driver Index.");
        }
    }

    private int selectSourceLocation() {
        List<String> locations = locationService.getLocationsDisplay();
        if (locations.isEmpty()) {
            System.out.println("No locations available.");
            return -1;
        }

        System.out.println("\n--- Select Source Location ---");
        for (int i = 0; i < locations.size(); i++)
            System.out.println("[" + (i+1) + "] " + locations.get(i));
        while (true) {
            int choice = promptInt("Enter Location ID: ")-1;
            if (choice >= 0 && choice < locations.size()) return choice;
            System.out.println("Invalid Location ID.");
        }
    }

    private Map<Integer, Map<Integer, Integer>> chooseSuppliersAndProducts() {

        Map<Integer, Map<Integer, Integer>> allocations = new HashMap<>();

        List<String> supplierNames = supplierService.getSuppliersDisplay();

        if (supplierNames == null || supplierNames.isEmpty()) {
            System.out.println("No suppliers available.");
            return allocations;
        }

        System.out.println("\n--- Available Suppliers ---");
        for (int i = 0; i < supplierNames.size(); i++) {
            System.out.println("[" + (i+1) + "] " + supplierNames.get(i));
        }

        System.out.print("\nSelect Supplier Indices (comma separated, e.g., '0, 2' or 'all'): ");
        String input = scanner.nextLine().trim();
        List<Integer> selectedSupplierIndices = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            for (int i = 0; i < supplierNames.size(); i++) {
                selectedSupplierIndices.add(i);
            }
        } else {
            for (String part : input.split(",\\s*")) {
                try {
                    int idx = Integer.parseInt(part)-1;
                    if (idx >= 0 && idx < supplierNames.size()) {
                        selectedSupplierIndices.add(idx);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        for (int supplierIdx : selectedSupplierIndices) {
            String supplierName = supplierNames.get(supplierIdx);
            System.out.println("\n>>> " + supplierName);

            List<String> productNames = supplierService.getProductNamesForSupplier(supplierIdx);
            if (productNames == null || productNames.isEmpty()) {
                System.out.println("This supplier has no products in stock.");
                continue;
            }

            Map<Integer, Integer> productsToBuy = new HashMap<>();

            while (true) {
                System.out.println("\nAvailable Products at " + supplierName);
                for (int i = 0; i < productNames.size(); i++) {
                    int stockLeft = supplierService.getProductStock(supplierIdx, i);
                    System.out.println("[" + (i+1) + "] " + productNames.get(i) +  " (In Stock: " + stockLeft + ")");
                }

                System.out.print("Enter Product Index to add (or type 'done'): ");
                String prodInput = scanner.nextLine().trim();
                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int pIdx = Integer.parseInt(prodInput)-1;
                    if (pIdx >= 0 && pIdx < productNames.size()) {

                        int qty = promptInt("Quantity to take: ");
                        if (qty > 0) {
                            int productIndex = productService.getIndexByName(productNames.get(pIdx));
                            productsToBuy.put(productIndex, productsToBuy.getOrDefault(productIndex, 0) + qty);
                        }   else
                            System.out.println("Quantity must be greater than 0.");

                    } else {
                        System.out.println("Invalid Product Index.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number or 'done'.");
                }
            }

            if (!productsToBuy.isEmpty()) {
                allocations.put(supplierIdx, productsToBuy);
            }
        }

        return allocations;
    }

    private int promptInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) { System.out.println("Invalid input. Please enter an integer."); }
        }
    }
}