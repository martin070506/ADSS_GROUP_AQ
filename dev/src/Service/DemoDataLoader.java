package Service;

import java.util.HashMap;
import java.util.Map;

public class DemoDataLoader {

    public static void load(CompanyManager manager, ProductCatalogService productService,
                            TruckService truckService, DriverService driverService,BranchService branchService) {

        // 1. Seed Core Catalog Products
        productService.addProduct("Apple", 150);
        productService.addProduct("Banana", 120);
        productService.addProduct("Milk", 6);
        productService.addProduct("Bread", 15);
        productService.addProduct("Eggs", 30);
        productService.addProduct("Cheese", 45);
        productService.addProduct("Chicken", 80);

        // 2. Seed Fleet Vehicles Inventory
        truckService.addTruck(101, "Isuzu Sumo", 3500, 7500, 2);
        truckService.addTruck(102, "Mercedes Sprinter", 2000, 4000, 1);
        truckService.addTruck(103, "Volvo FH", 8000, 25000, 3);
        truckService.addTruck(104, "Renault Master", 2200, 4500, 1);

        // 3. Seed Registered Operators Workforce
        driverService.addDriver("Bob", 2);
        driverService.addDriver("Charlie", 1);
        driverService.addDriver("David", 3);
        driverService.addDriver("Eve", 2);

        // 4. Seed Corporate Physical Facilities Boundaries
        manager.addBranchLocation("Ashdod", "08-222-9900", "Grace");
        manager.addBranchLocation("Jerusalem", "02-555-8800", "Heidi");
        manager.addBranchLocation("Haifa", "04-333-7700", "Ivan");
        manager.addBranchLocation("Eilat", "08-999-6600", "Judy");

        // 5. Seed External Supplier Stock Inventories
        try {
            Map<Integer, Integer> stockTelAviv = new HashMap<>();
            stockTelAviv.put(0, 100); // Apple
            stockTelAviv.put(1, 100); // Banana
            stockTelAviv.put(2, 50);  // Milk
            manager.registerSupplierByIndices("Tel Aviv", "03-123-9901", "Alice", stockTelAviv);

            Map<Integer, Integer> stockPetahTikva = new HashMap<>();
            stockPetahTikva.put(3, 200); // Bread
            stockPetahTikva.put(4, 150); // Eggs
            manager.registerSupplierByIndices("Petah Tikva", "03-555-4433", "Frank", stockPetahTikva);

            Map<Integer, Integer> stockRishon = new HashMap<>();
            stockRishon.put(5, 80);  // Cheese
            stockRishon.put(6, 120); // Chicken
            manager.registerSupplierByIndices("Rishon LeZion", "03-888-2211", "George", stockRishon);

        } catch (Exception e) {
            System.err.println("Error seeding supplier initialization profiles: " + e.getMessage());
        }
    }
}