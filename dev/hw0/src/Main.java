import Domain.*;
import Presentation.MainConsole;
import Service.BranchManager;
import Service.CompanyManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        List<Location> locations = new LinkedList<>();
        List<Product> products = new LinkedList<>();
        List<Supplier> suppliers = new LinkedList<>();
        List<Truck> trucks = new LinkedList<>();
        List<Driver> drivers = new LinkedList<>();

        fillLists(locations, products, suppliers, trucks, drivers);

        CompanyManager companyManager = CompanyManager.getInstance(trucks, drivers, suppliers, locations);

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Branch Shipment Requests Setup ===");

        while (true) {
            System.out.print("\nWould you like to add a request for a branch? (yes/no): ");
            String ans = scanner.nextLine().trim();

            if (ans.equalsIgnoreCase("no") || ans.equalsIgnoreCase("n")) {
                break;
            } else if (!ans.equalsIgnoreCase("yes") && !ans.equalsIgnoreCase("y")) {
                System.out.println("Invalid input. Please type 'yes' or 'no'.");
                continue;
            }

            System.out.println("\n--- Select Branch Location ---");
            for (int i = 0; i < locations.size(); i++)
                System.out.println("[" + (i + 1) + "] " + locations.get(i).address() + " (Contact: " + locations.get(i).contactName() + ")");

            BranchManager currentBranch = null;
            while (currentBranch == null) {
                System.out.print("Enter location index: ");
                try {
                    int locIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (locIndex >= 0 && locIndex < locations.size())
                        currentBranch = new BranchManager(locations.get(locIndex));
                    else
                        System.out.println("Invalid index. Try again.");

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            List<ProductPair> requestedItems = new LinkedList<>();
            System.out.println("\n--- Select Products for " + currentBranch.getLocation().address() + " ---");

            while (true) {
                System.out.println("\nAvailable Products:");
                for (int i = 0; i < products.size(); i++) {
                    System.out.println("[" + (i + 1) + "] " + products.get(i).name());
                }
                System.out.print("Enter product index to add (or type 'done' to finish branch request): ");
                String prodInput = scanner.nextLine().trim();

                if (prodInput.equalsIgnoreCase("done")) {
                    break;
                }

                try {
                    int prodIndex = Integer.parseInt(prodInput) - 1;
                    if (prodIndex >= 0 && prodIndex < products.size()) {
                        Product selectedProduct = products.get(prodIndex);

                        System.out.print("Enter quantity for " + selectedProduct.name() + ": ");
                        int quantity = Integer.parseInt(scanner.nextLine().trim());

                        if (quantity > 0) {
                            requestedItems.add(new ProductPair(selectedProduct, quantity));
                            System.out.println("Added " + quantity + " x " + selectedProduct.name() + ".");
                        } else {
                            System.out.println("Quantity must be greater than 0.");
                        }
                    } else {
                        System.out.println("Invalid product index. Try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }

            if (!requestedItems.isEmpty()) {
                currentBranch.requestShipment(requestedItems);
                System.out.println("\nRequest for branch " + currentBranch.getLocation().address() + " has been successfully submitted!");
            } else {
                System.out.println("\nNo products selected. Request cancelled.");
            }
        }

        System.out.println("\n======================================");
        System.out.println("Starting Main System Console...");
        System.out.println("======================================\n");

        MainConsole console = new MainConsole(companyManager, trucks, drivers, locations, suppliers);
        console.run();
    }

    public static void fillLists(List<Location> locations, List<Product> products,
                                 List<Supplier> suppliers, List<Truck> trucks, List<Driver> drivers) {

        locations.add(new Location("Tel Aviv", "03-1234567", "Alice"));     // 0
        locations.add(new Location("Holon", "03-7654321", "Bob"));          // 1
        locations.add(new Location("Haifa", "04-1112233", "Charlie"));      // 2
        locations.add(new Location("Netanya", "09-4445566", "David"));      // 3
        locations.add(new Location("Beer Sheva", "08-9998877", "Eve"));     // 4
        locations.add(new Location("Jerusalem", "02-5556677", "Frank"));    // 5
        locations.add(new Location("Ashdod", "08-2223344", "Grace"));       // 6
        locations.add(new Location("Petah Tikva", "03-9990011", "Henry"));  // 7
        locations.add(new Location("Ramat Gan", "03-4445556", "Isabella")); // 8
        locations.add(new Location("Rehovot", "08-7778899", "Jack"));       // 9

        products.add(new Product("Apple", 150));      // 0
        products.add(new Product("Banana", 120));     // 1
        products.add(new Product("Orange", 200));     // 2
        products.add(new Product("Lemon", 80));       // 3
        products.add(new Product("Pineapple", 900));  // 4
        products.add(new Product("Kiwi", 250));       // 5
        products.add(new Product("Tomato", 10));      // 6
        products.add(new Product("Cucumber", 8));     // 7
        products.add(new Product("Milk", 6));         // 8
        products.add(new Product("Bread", 12));       // 9

        suppliers.add(new Supplier(locations.get(0), new LinkedList<>(List.of(new ProductPair(products.get(0), 100)))));
        suppliers.add(new Supplier(locations.get(1), new LinkedList<>(List.of(new ProductPair(products.get(1), 100)))));
        suppliers.add(new Supplier(locations.get(2), new LinkedList<>(List.of(new ProductPair(products.get(2), 100)))));
        suppliers.add(new Supplier(locations.get(3), new LinkedList<>(List.of(new ProductPair(products.get(3), 100)))));
        suppliers.add(new Supplier(locations.get(4), new LinkedList<>(List.of(new ProductPair(products.get(4), 100)))));
        suppliers.add(new Supplier(locations.get(5), new LinkedList<>(List.of(new ProductPair(products.get(5), 100)))));

        suppliers.add(new Supplier(locations.get(6), new LinkedList<>(List.of(
                new ProductPair(products.get(6), 80),
                new ProductPair(products.get(7), 80),
                new ProductPair(products.get(8), 80)
        ))));

        suppliers.add(new Supplier(locations.get(7), new LinkedList<>(List.of(
                new ProductPair(products.get(5), 150),
                new ProductPair(products.get(0), 200)
        ))));

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