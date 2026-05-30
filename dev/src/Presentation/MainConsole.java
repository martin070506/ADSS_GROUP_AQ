package Presentation;

import Domain.Product;
import Domain.Supplier;
import Exceptions.*;
import Service.CompanyManager;
import java.util.*;

public class MainConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CompanyManager companyManager;

    public MainConsole(CompanyManager companyManager) {
        this.companyManager = companyManager;
        System.out.println("Welcome to the main console");
    }

    public void run() {
        System.out.println("Welcome to the Shipment System!");
        while (true) {
            System.out.println("\n----------------------------------");
            System.out.print("Would you like to start a new shipment? (yes/no): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("n")) {
                System.out.println("Exiting the system. Goodbye!");
                throw new ConsoleEndException("Exiting the system. Goodbye!");
            } else if (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("y")) {
                System.out.println("Invalid input, please type 'yes' or 'no'.");
                continue;
            }
            initiateShipment();
        }
    }

    public void initiateShipment() {
        int truckIndex = chooseTruck();
        if (truckIndex == -1) return;

        int driverIndex = chooseDriver();
        if (driverIndex == -1) return;

        int sourceIndex = selectSourceLocation();
        if (sourceIndex == -1) return;

        Map<Supplier, Map<Product, Integer>> supplierAllocationsIds = chooseSuppliersAndProducts();
        if (supplierAllocationsIds.isEmpty()) return;

        try {
            int transportId = companyManager.createTransportAndGetId(truckIndex, driverIndex, sourceIndex, supplierAllocationsIds);
            System.out.println("Transport Created ID: " + transportId);
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
            System.out.println(companyManager.getTransportById(transportId).getTransportFile().toString());
            companyManager.finishShipment(transportId);
            System.out.println("Shipment finished successfully!");
        }
    }

    public void handleOverweightUI(int transportId) {
        String supplierName = companyManager.getFirstSupplierName(transportId);
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
                System.out.println("[" + i + "] " + productName + " (" + currentItemsDisplay.get(productName) + " units)");
                i++;
            }

            System.out.print("Enter product ID to remove (or type 'done'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;

            try {
                int productId = Integer.parseInt(input);
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
        List<String> trucks = companyManager.getAvailableTrucksDisplay();
        if (trucks.isEmpty()) {
            System.out.println("No trucks available.");
            return -1;
        }

        System.out.println("\n--- Available Trucks ---");
        for (int i = 0; i < trucks.size(); i++)
            System.out.println("[" + i + "] " + trucks.get(i));
        while (true) {
            int choice = promptInt("Enter Truck ID: ");
            if (choice >= 0 && choice < trucks.size()) return choice;
            System.out.println("Invalid Truck ID.");
        }
    }

    private int chooseDriver() {
        List<String> drivers = companyManager.getAvailableDriversDisplay();
        if (drivers.isEmpty()) {
            System.out.println("No eligible drivers available for this truck.");
            return -1;
        }

        System.out.println("\n--- Available Drivers ---");
        for (int i = 0; i < drivers.size(); i++)
            System.out.println("[" + i + "] " + drivers.get(i));
        while (true) {
            int choice = promptInt("Enter Driver ID: ");
            if (choice >= 0 && choice < drivers.size()) return choice;
            System.out.println("Invalid Driver ID.");
        }
    }

    private int selectSourceLocation() {
        List<String> locations = companyManager.getAllLocationsDisplay();
        if (locations.isEmpty()) {
            System.out.println("No locations available.");
            return -1;
        }

        System.out.println("\n--- Select Source Location ---");
        for (int i = 0; i < locations.size(); i++)
            System.out.println("[" + i + "] " + locations.get(i));
        while (true) {
            int choice = promptInt("Enter Location ID: ");
            if (choice >= 0 && choice < locations.size()) return choice;
            System.out.println("Invalid Location ID.");
        }
    }

    private Map<Supplier, Map<Product, Integer>> chooseSuppliersAndProducts() {
        Map<Supplier, Map<Product, Integer>> allocations = new HashMap<>();

        // 1. Grab your raw supplier list directly from the manager
        List<Supplier> allSuppliers = companyManager.getAllSuppliers();

        if (allSuppliers == null || allSuppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return allocations;
        }

        System.out.println("\n--- Available Suppliers ---");
        for (int i = 0; i < allSuppliers.size(); i++) {
            Supplier s = allSuppliers.get(i);
            System.out.println("[" + i + "] " + s.getName() + " (" + s.getSupplierLocation() + ")");
        }

        // 2. Select which suppliers you want to buy from
        System.out.print("\nSelect Supplier Indices (comma separated, e.g., '0, 2' or 'all'): ");
        String input = scanner.nextLine().trim();
        List<Supplier> selectedSuppliers = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            selectedSuppliers.addAll(allSuppliers);
        } else {
            for (String part : input.split(",\\s*")) {
                try {
                    int idx = Integer.parseInt(part);
                    if (idx >= 0 && idx < allSuppliers.size()) {
                        selectedSuppliers.add(allSuppliers.get(idx)); // Instantly convert index to Supplier object
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // 3. For each selected supplier, look inside their personal stock map
        for (Supplier supplier : selectedSuppliers) {
            System.out.println("\n>>> Shopping from Supplier: " + supplier.getName() + " <<<");

            // Grab the supplier's available products map
            Map<Product, Integer> availableStock = supplier.getProductsAvailable();
            if (availableStock == null || availableStock.isEmpty()) {
                System.out.println("This supplier has no products in stock.");
                continue;
            }

            // Convert the map keys to an indexed list so the user can type '0', '1', '2' to pick an item
            List<Product> productCatalog = new ArrayList<>(availableStock.keySet());
            Map<Product, Integer> productsToBuy = new HashMap<>();

            while (true) {
                System.out.println("\nAvailable Products at this supplier:");
                for (int i = 0; i < productCatalog.size(); i++) {
                    Product p = productCatalog.get(i);
                    int stockLeft = availableStock.get(p); // Pull total available stock from map
                    System.out.println("[" + i + "] " + p.name() +  " (In Stock: " + stockLeft + ")");
                }

                System.out.print("Enter Product Index to add (or type 'done'): ");
                String prodInput = scanner.nextLine().trim();
                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int pIdx = Integer.parseInt(prodInput);
                    if (pIdx >= 0 && pIdx < productCatalog.size()) {
                        Product selectedProduct = productCatalog.get(pIdx); // Instantly convert index to Product object
                        int maxAvailable = availableStock.get(selectedProduct);

                        int qty = promptInt("Quantity to take: ");
                        if (qty > 0) {
                            if (qty <= maxAvailable) {
                                // Store the Product object directly as the key
                                productsToBuy.put(selectedProduct, productsToBuy.getOrDefault(selectedProduct, 0) + qty);
                                System.out.println("Added " + qty + "x " + selectedProduct.name() + " to cart.");
                            } else {
                                System.out.println("Error: Insufficient stock. Only " + maxAvailable + " units available.");
                            }
                        } else {
                            System.out.println("Quantity must be greater than 0.");
                        }
                    } else {
                        System.out.println("Invalid Product Index.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number or 'done'.");
                }
            }

            // If items were selected, save the supplier allocation
            if (!productsToBuy.isEmpty()) {
                allocations.put(supplier, productsToBuy);
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