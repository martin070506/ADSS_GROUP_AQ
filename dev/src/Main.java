import Domain.*;
import Service.*;
import Presentation.MainConsole;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // Core Data
    private static List<Location> locations = new LinkedList<>();
    private static List<Product> products = new LinkedList<>();
    private static List<Supplier> suppliers = new LinkedList<>();
    private static List<Truck> trucks = new LinkedList<>();
    private static List<Driver> drivers = new LinkedList<>();
    private static List<BranchManager> branchManagers = new LinkedList<>();

    public static void main(String[] args) {
        System.out.println("=== LOGISTICS MANAGEMENT SYSTEM ===");
        System.out.println("1. Load Automatic Demo Data");
        System.out.println("2. Manual Data Entry");
        System.out.print("Choice: ");

        if (scanner.nextLine().equals("1")) {
            fillListsWithDemoData();
        } else {
            manualSetup();
        }

        // Initialize our Facades/Managers
        CompanyManager companyManager = CompanyManager.getInstance(trucks, drivers, suppliers, locations,branchManagers);
        // Assuming your MainConsole takes these as dependencies
        MainConsole console = new MainConsole(companyManager);

        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": addBranchRequest(); break;
                case "2": resupplySupplier(); break;
                case "3": setupSuppliers(companyManager); break;
                case "4": addStoreLocation(companyManager); break;
                case "5": setupDrivers(companyManager); break;
                case "6": setupTrucks(companyManager); break;
                case "7": runShipmentCycle(console); break;
                case "0":
                    System.out.println("Exiting system...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n===============================");
        System.out.println("       OPERATIONS MENU");
        System.out.println("===============================");
        System.out.println("[1] Add Request for Branch");
        System.out.println("[2] Resupply a Supplier");
        System.out.println("[3] Add New Supplier");
        System.out.println("[4] Add New Store Location");
        System.out.println("[5] Add New Driver");
        System.out.println("[6] Add New Truck");
        System.out.println("[7] START SHIPMENT CONSOLE");
        System.out.println("[0] Exit");
        System.out.print(">> Select Option: ");
    }

    // --- MANUAL SETUP MODULES ---

    private static void manualSetup() {
        System.out.println("\n--- BEGIN MANUAL DATA ENTRY ---");
        setupProducts(); // Must come first to stock suppliers
        setupDrivers(CompanyManager.getInstance());
        setupTrucks(CompanyManager.getInstance());
        setupLocations();
        setupStoreLocations();
        setupSuppliers(CompanyManager.getInstance());
    }

    private static void setupProducts() {
        System.out.println("\n> Define Product Catalog");
        while (true) {
            System.out.print("Product Name (or 'done'): ");
            String name = scanner.nextLine();
            if (name.equalsIgnoreCase("done")) break;
            int weight = promptInt("Unit Weight: ");
            products.add(new Product(name, weight));
        }
    }

    private static void setupDrivers(CompanyManager companyManager) {
        System.out.print("\nAdd Driver? (y/n): ");
        while (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            int lic = promptInt("License Level (1-3): ");
            Driver driver = new Driver(name, lic);
            companyManager.addDriver(driver);
            System.out.print("Add another driver? (y/n): ");
        }
    }

    private static void setupTrucks(CompanyManager companyManager) {
        System.out.print("\nAdd Truck? (y/n): ");
        while (scanner.nextLine().equalsIgnoreCase("y")) {
            int id = promptInt("Truck ID: ");
            System.out.print("Model: ");
            String model = scanner.nextLine();
            int weight = promptInt("Net Weight: ");
            int max = promptInt("Max Capacity: ");
            int lic = promptInt("License Required (1-3): ");
            Truck truck = new Truck(id, model, weight, max, lic);
            companyManager.addTruck(truck);
            System.out.print("Add another truck? (y/n): ");
        }
    }

    private static void setupLocations() {
        System.out.println("\n> Define General Locations");
        while (true) {
            System.out.print("Location Address (or 'done'): ");
            String addr = scanner.nextLine();
            if (addr.equalsIgnoreCase("done")) break;
            System.out.print("Phone: ");
            String phone = scanner.nextLine();
            System.out.print("Contact Name: ");
            String contact = scanner.nextLine();
            locations.add(new Location(addr, phone, contact));
        }
    }

    private static void setupStoreLocations() {
        System.out.println("\n> Designate Store Branches");
        for (Location loc : locations) {
            System.out.print("Is [" + loc.address() + "] a Store? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                branchManagers.add(new BranchManager(loc));
            }
        }
    }

    private static void setupSuppliers(CompanyManager companyManager) {
        System.out.println("\n> Designate Suppliers");
        for (Location loc : locations) {
            System.out.print("Is [" + loc.address() + "] a Supplier? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                List<ProductPair> stock = new LinkedList<>();
                System.out.println("-- Adding Stock for " + loc.address() + " --");
                for (Product p : products) {
                    System.out.print("Does it supply " + p.name() + "? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        int qty = promptInt("Initial Quantity: ");
                        stock.add(new ProductPair(p, qty));
                    }
                }
                Supplier supplier = new Supplier(loc, stock);
                companyManager.addSupplier(supplier);
            }
        }
    }
    private  static void addStoreLocation(CompanyManager companyManager) {
        System.out.println("\n> Define General Locations");
        while (true) {
            System.out.print("Location Address (or 'done'): ");
            String addr = scanner.nextLine();
            if (addr.equalsIgnoreCase("done")) break;
            System.out.print("Phone: ");
            String phone = scanner.nextLine();
            System.out.print("Contact Name: ");
            String contact = scanner.nextLine();
            Location loc = new Location(addr, phone, contact);
            companyManager.addLocation(loc);
            companyManager.addBranchManager(new BranchManager(loc));
        }
    }

    // --- OPERATIONAL LOGIC ---

    private static void addBranchRequest() {
        if (branchManagers.isEmpty()) {
            System.out.println("Error: No branch locations available.");
            return;
        }
        System.out.println("\nSelect Branch:");
        for (int i = 0; i < branchManagers.size(); i++) {
            System.out.println("[" + i + "] " + branchManagers.get(i).getLocation().address());
        }
        int choice = promptInt("Choice Index: ");
        BranchManager bm = branchManagers.get(choice);

        List<ProductPair> requested = new LinkedList<>();
        while (true) {
            System.out.println("\nAvailable Products:");
            for (int i = 0; i < products.size(); i++) System.out.println("[" + i + "] " + products.get(i).name());
            System.out.print("Select Product Index (or 'done'): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) break;

            int pIdx = Integer.parseInt(input);
            int qty = promptInt("Quantity needed: ");
            requested.add(new ProductPair(products.get(pIdx), qty));
        }
        bm.requestShipment(requested);
        System.out.println("Request added for branch.");
    }

    private static void resupplySupplier() {
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers available.");
            return;
        }
        System.out.println("\nSelect Supplier to Restock:");
        for (int i = 0; i < suppliers.size(); i++) {
            System.out.println("[" + i + "] " + suppliers.get(i).getSupplierLocation().address());
        }
        int sIdx = promptInt("Supplier Index: ");
        Supplier s = suppliers.get(sIdx);

        System.out.println("Select Product:");
        for (int i = 0; i < products.size(); i++) System.out.println("[" + i + "] " + products.get(i).name());
        int pIdx = promptInt("Product Index: ");
        int qty = promptInt("Amount to add to stock: ");

        // Facade/Supplier logic to add stock
        s.addStock(products.get(pIdx), qty);
        System.out.println("Stock updated successfully.");
    }

    private static void runShipmentCycle(MainConsole console) {
        System.out.println("\n--- Initiating Shipment Console ---");
        try {
            console.run();
        } catch (Exception e) {
            System.out.println("Console session closed.");
        }
    }

    // --- UTILS ---

    private static int promptInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a number.");
            }
        }
    }

    public static void fillListsWithDemoData() {
        locations.add(new Location("Tel Aviv", "03-1234567", "Alice"));
        locations.add(new Location("Holon", "03-7654321", "Bob"));
        locations.add(new Location("Haifa", "04-1112233", "Charlie"));
        locations.add(new Location("Netanya", "09-4445566", "David"));
        locations.add(new Location("Beer Sheva", "08-9998877", "Eve"));
        locations.add(new Location("Jerusalem", "02-5556677", "Frank")); // 5
        locations.add(new Location("Ashdod", "08-2223344", "Grace")); // 6
        locations.add(new Location("Petah Tikva", "03-9990011", "Henry")); // 7
        locations.add(new Location("Ramat Gan", "03-4445556", "Isabella")); // 8
        locations.add(new Location("Rehovot", "08-7778899", "Jack")); // 9



        products.add(new Product("Apple", 150)); // 0
        products.add(new Product("Banana", 120)); // 1
        products.add(new Product("Orange", 200)); // 2
        products.add(new Product("Lemon", 80)); // 3
        products.add(new Product("Pineapple", 900)); // 4
        products.add(new Product("Kiwi", 250)); // 5
        products.add(new Product("Tomato", 10)); // 6
        products.add(new Product("Cucumber", 8)); // 7
        products.add(new Product("Milk", 6)); // 8
        products.add(new Product("Bread", 12)); // 9



        suppliers.add(new Supplier(locations.get(0), new LinkedList<>(List.of(new ProductPair(products.get(0), 100)))));
        suppliers.add(new Supplier(locations.get(1), new LinkedList<>(List.of(new ProductPair(products.get(1), 100)))));
        suppliers.add(new Supplier(locations.get(2), new LinkedList<>(List.of(new ProductPair(products.get(2), 100)))));
        suppliers.add(new Supplier(locations.get(3), new LinkedList<>(List.of(new ProductPair(products.get(3), 100)))));


        suppliers.add(new Supplier(locations.get(4), new LinkedList<>(List.of(
                new ProductPair(products.get(4), 80),
                new ProductPair(products.get(5), 80),
                new ProductPair(products.get(6), 80)
        ))));



        suppliers.add(new Supplier(locations.get(5), new LinkedList<>(List.of(
                new ProductPair(products.get(7), 150),
                new ProductPair(products.get(8), 200)
        ))));




        branchManagers.add(new BranchManager(locations.get(6)));
        branchManagers.add(new BranchManager(locations.get(7)));
        branchManagers.add(new BranchManager(locations.get(8)));
        branchManagers.add(new BranchManager(locations.get(9)));



        trucks.add(new Truck(101, "Isuzu Sumo", 3500, 7500, 2));
        trucks.add(new Truck(102, "DAF LF", 5000, 12000, 2));
        trucks.add(new Truck(103, "Volvo FH", 8000, 26000, 3));
        trucks.add(new Truck(104, "Mercedes Actros", 9500, 32000, 3));
        trucks.add(new Truck(105, "Scania R500", 10000, 40000, 3));


        drivers.add(new Driver("Alice", 1));
        drivers.add(new Driver("Bob", 2));
        drivers.add(new Driver("Charlie", 2));
        drivers.add(new Driver("David", 3));
        drivers.add(new Driver("Eve", 3));

    }
}