package Presentation.Transportation;

import Exceptions.*;
import Service.Transportation.*;
import Service.Workers.ShiftPlacementService;
import Service.Workers.ShiftWorkersCanidatesService;
import Service.Workers.WorkersService;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.*;

public class MainConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final TransportManagerService transportService;
    private final SupplierService supplierService;
    private final ProductCatalogService productService;
    private final TruckService truckService;
    private final LocationService locationService;
    private final WorkersService workers_service;
    private final ShiftWorkersCanidatesService candidates_service;
    private final ShiftPlacementService placement_service;

    public MainConsole(TransportManagerService transportService, SupplierService supplierService,
                       ProductCatalogService productService, TruckService truckService, LocationService locationService,
                       WorkersService workers_service, ShiftWorkersCanidatesService candidates_service,
                       ShiftPlacementService placement_service) {
        this.transportService = transportService;
        this.supplierService = supplierService;
        this.productService = productService;
        this.truckService = truckService;
        this.locationService = locationService;
        this.workers_service = workers_service;
        this.candidates_service = candidates_service;
        this.placement_service = placement_service;
    }

    public void initiateShipment() {

        boolean isMorning = LocalTime.now().isAfter(LocalTime.of(4, 59)) && LocalTime.now().isBefore(LocalTime.of(17, 0));

        int sourceIdx = selectSourceLocation();
        if (sourceIdx == -1) return;

        int truckId = chooseTruck();
        if (truckId == -1) return;

        int driverId = 0; // chooseDriver(sourceIdx, truckId, isMorning) Todo: Fix
        if (driverId == -1) return;

        Map<Integer, Map<Integer, Integer>> supplierAllocationsIds = chooseSuppliersAndProducts();

        if (supplierAllocationsIds.isEmpty()) {
            System.out.println("No suppliers selected.");
            return;
        }
        try {
            transportService.createTransport(truckId, driverId, sourceIdx,
                    supplierAllocationsIds, truckService.getTruckDisplay(truckId), 
                    locationService.getLocationDisplay(sourceIdx));
            processShipmentFlow(isMorning);
        } catch (DomainException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Critical System Error: " + e.getMessage());
        }
    }

    private void processShipmentFlow(boolean isMorning) {
        transportService.startShipment();

        boolean shipmentFinish = false;
        while (!shipmentFinish) {
            try {
                transportService.processTransportLifecycle(isMorning);
                shipmentFinish = true;
            } catch (OverweightException oe) {
                handleOverweightUI();
            } catch (InsufficientSupplierStockException | InsufficientTruckStockException ise) {
                System.out.println("Stock Problem: " + ise.getMessage());
                try {
                    transportService.handleStockException(ise);
                } catch (Exception e) {
                    System.out.println("Error handling stock: " + e.getMessage());
                }
            } catch (MissingShopKeeper msk) {
                System.out.println("ShopKeeper Missing: " + msk.getMessage());
                transportService.skipRequest("ShopKeeper Missing");
            } catch (DomainException de) {
                System.out.println("General Domain Error: " + de.getMessage());
                break;
            } catch (Exception e) {
                System.out.println("General Error: " + e.getMessage());
                break;
            }
        }

        if (shipmentFinish) {
            System.out.println(transportService.getTransportFileDisplay());
            transportService.finishShipment();
            System.out.println("Shipment finished successfully!");
        }
    }

    public void handleOverweightUI() {
        String supplierName = transportService.getFirstSupplierName();
        int choice;

        while (true) {
            System.out.println("Truck is overweight at " + supplierName);
            System.out.println("1. Skip this supplier");
            System.out.println("2. Emergency Drop-off");
            System.out.println("3. Fine-tune: Remove specific items");
            System.out.println("4. Switch Truck");
            System.out.print("Choose an option: ");
            String choiceS = scanner.nextLine().trim();
            if (choiceS.equalsIgnoreCase("1") || choiceS.equalsIgnoreCase("2") || choiceS.equalsIgnoreCase("3") || choiceS.equalsIgnoreCase("4")) {
                choice = Integer.parseInt(choiceS);
                break;
            }
            System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
        }

        try {
            switch (choice) {
                case 3:
                    getItemsToRemoveUI();
                    break;
                case 4:
                    int driverLicense = workers_service.getLicense(transportService.getDriverId());

                    List<Integer> truckIds = truckService.getBiggerTruckIds(driverLicense,
                            transportService.getTruckId());
                    while (!truckIds.isEmpty()) {
                        for (int truckId : truckIds)
                            System.out.println(truckService.getTruckDisplay(truckId));

                        System.out.print("Enter Truck ID to replace: ");
                        int newTruckId = Integer.parseInt(scanner.nextLine().trim());
                        if (truckIds.contains(newTruckId)) {
                            transportService.replaceTruck(newTruckId);
                            transportService.finalizeCurrentSupplierLoading();
                            return;
                        } else {
                            System.out.println("Invalid Truck ID. Please try again.");
                        }
                    }
                    break;
                default:
                    transportService.resolveOverweightIssue(choice);
            }
            System.out.println("Mitigation failed: No alternative vehicle matches criteria. Skipping supplier.");
            transportService.skipSupplier("No alternative vehicle matches criteria");
        } catch (Exception e) { System.out.println("Error handling overweight: " + e.getMessage()); }
    }

    private void getItemsToRemoveUI() {
        while (true) {
            Map<Integer, Integer> currentItemsDisplay = truckService.getTruckProducts(transportService.getTruckId());
            if (currentItemsDisplay.isEmpty()) {
                System.out.println("The truck is now empty!");
                break;
            }

            System.out.println("\nCurrent loaded items:");
            int i = 0;
            List<Integer> productNames = new ArrayList<>(currentItemsDisplay.keySet());
            for (Integer productId : productNames)
                System.out.println(productService.getProductDisplay(productId) + " (" + currentItemsDisplay.get(productId) + " units)");


            System.out.print("Enter product ID to remove (or type '-1'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("-1"))
                break;

            try {
                int productId = Integer.parseInt(input);
                if (productId >= 0 && productId < productNames.size()) {
                    int amt = promptInt("Amount to remove: ");
                    if (amt > 0) {
                        transportService.resolveOverweightWithFineTuning(productId, amt);
                        System.out.println("Items removed.");
                    } else { System.out.println("Invalid amount."); }
                } else { System.out.println("Product ID not found on truck."); }
            } catch (NumberFormatException e) { System.out.println("Invalid input. Please enter a number."); }
        }
    }

    private int chooseTruck() {
        List<Integer> trucks = truckService.getAvailableTruckIds();
        if (trucks.isEmpty()) {
            System.out.println("No trucks available.");
            return -1;
        }
        
        while (true) {
        System.out.println("\n--- Available Trucks ---");
        for (Integer truck : trucks) 
            System.out.println(truckService.getTruckDisplay(truck));
            int choice = promptInt("Enter Truck: ");
            if (trucks.contains(choice))
                return choice;
            System.out.println("Invalid Truck Index.");
        }
    }

    private int chooseDriver(int sourceIdx, int truckId, boolean isMorning) {
        LocalDate today = LocalDate.now();
        List<Integer> drivers = candidates_service.getAllAvialableDrivers(today, isMorning, locationService.getLocation(sourceIdx)); // TODO: notice break of Domain
        if (drivers.isEmpty()) {
            System.out.println("No drivers available.");
            return -1;
        }
        

        System.out.println("\n--- Available Drivers ---");
        for (int index : drivers)
            System.out.println("fuuuuu"); // "Driver: " + workers_service.getName(index) + ", License: " + workers_service.getLicense(index) Todo: Fix

        while (true) {
            int driverIndex = promptInt("Enter Driver: ")-1;
            if (driverIndex == -1)
                return -1;

            if (!drivers.contains(driverIndex)) {
                System.out.println("Invalid Driver Index.");
                continue;
            }

            if (!truckService.isDriverEligible(workers_service.getLicense(driverIndex), truckId)) {
                System.out.println("Driver is not eligible to this truck.");
                continue;
            }

            String massage = placement_service.PlaceDriver(today, isMorning,
                    locationService.getLocation(sourceIdx), drivers.get(driverIndex));

            if (massage.startsWith("failed")) {
                System.out.println(massage);
                continue;
            }

            return driverIndex;
        }
    }

    private int selectSourceLocation() {
        List<Integer> locations = locationService.getLocationIds();
        if (locations.isEmpty()) {
            System.out.println("No locations available.");
            return -1;
        }

        System.out.println("\n--- Select Source Location ---");
        for (Integer location : locations) 
            System.out.println(locationService.getLocationDisplay(location));
        while (true) {
            int choice = promptInt("Enter Location ID: ");
            if (locations.contains(choice)) 
                return choice;
            System.out.println("Invalid Location ID.");
        }
    }

    private Map<Integer, Map<Integer, Integer>> chooseSuppliersAndProducts() {

        Map<Integer, Map<Integer, Integer>> allocations = new HashMap<>();

        List<Integer> supplierIds = supplierService.getSupplierIds();

        if (supplierIds == null || supplierIds.isEmpty()) {
            System.out.println("No suppliers available.");
            return allocations;
        }

        System.out.println("\n--- Available Suppliers ---");
        for (Integer supplierId : supplierIds) 
            System.out.println(supplierService.getSupplierDisplay(supplierId));

        System.out.print("\nSelect Supplier Indices (comma separated, e.g., '0, 2' or 'all'): ");
        String input = scanner.nextLine().trim();
        List<Integer> selectedSupplierIndices = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            for (int i = 0; i < supplierIds.size(); i++) {
                selectedSupplierIndices.add(i);
            }
        } else {
            for (String part : input.split(",\\s*")) {
                try {
                    int idx = Integer.parseInt(part);
                    if (idx >= 0) {
                        selectedSupplierIndices.add(idx);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        int supS = selectedSupplierIndices.size();
        for (int i = 0; i < selectedSupplierIndices.size(); i++) {
            if (!supplierIds.contains(selectedSupplierIndices.get(i))) {
                selectedSupplierIndices.remove(i);
                i--;
            }
        }
        if (selectedSupplierIndices.size() != supS)
            System.out.println("Invalid Supplier Indices. Only " + selectedSupplierIndices.size() + " valid.");

        for (int supplierId : selectedSupplierIndices) {
            String supplierName = supplierService.getSupplierName(supplierId);
            System.out.println("\n>>> " + supplierName);

            List<Integer> productNames = supplierService.getProductIds(supplierId);//this includes the product id
            if (productNames == null || productNames.isEmpty()) {
                System.out.println("This supplier has no products in stock.");
                continue;
            }

            Map<Integer, Integer> productsToBuy = new HashMap<>();

            while (true) {
                System.out.println("\nAvailable Products at " + supplierName);
                List<Integer> availableProductIds = supplierService.getProductIds(supplierId);
                for (Integer i: availableProductIds) {
                    int stockLeft = supplierService.getProductStock(supplierId, i);
                    System.out.println(productService.getProductDisplay(i) +  " (In Stock: " + stockLeft + ")");
                }

                System.out.print("Enter Product Index to add (or type 'done'): ");
                String prodInput = scanner.nextLine().trim();
                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int pIdx = Integer.parseInt(prodInput);
                    if (pIdx >= 0) {

                        int qty = promptInt("Quantity to take: ");
                        if (qty > 0) {

                            productsToBuy.put(pIdx, productsToBuy.getOrDefault(pIdx, 0) + qty);
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
                allocations.put(supplierId, productsToBuy);
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
